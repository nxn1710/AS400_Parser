#!/usr/bin/env python3
"""
RPG3 Parser CLI — Python wrapper for the Java parser library.

Usage:
    rpg3-parser parse SOURCE [-o OUTPUT] [--copy-path PATHS] [--charset CHARSET]
    rpg3-parser batch SOURCE_DIR [-o OUTPUT_DIR] [--copy-path PATHS] [--parallel N]
    rpg3-parser validate JSON_FILE
"""

import argparse
import json
import subprocess
import sys
from pathlib import Path
from concurrent.futures import ThreadPoolExecutor, as_completed

# Default JAR location (relative to this script)
DEFAULT_JAR = Path(__file__).parent.parent / "parser-core" / "build" / "libs" / "as400-parser-core-1.0.0-SNAPSHOT-all.jar"


def find_jar():
    """Find the fat JAR file."""
    if DEFAULT_JAR.exists():
        return DEFAULT_JAR
    # Try alternate name
    alt = DEFAULT_JAR.parent / "as400-parser-core-all.jar"
    if alt.exists():
        return alt
    print(f"Error: JAR not found at {DEFAULT_JAR}", file=sys.stderr)
    print("Run 'gradlew shadowJar' to build the JAR first.", file=sys.stderr)
    sys.exit(1)


def run_parser(jar_path, source_path, charset="UTF-8", copy_path=None):
    """Invoke the Java parser and return the JSON output."""
    cmd = [
        "java", "-jar", str(jar_path),
        "--source", str(source_path),
        "--charset", charset,
    ]
    if copy_path:
        cmd.extend(["--copy-path", copy_path])

    result = subprocess.run(cmd, capture_output=True, text=True, timeout=60)
    if result.returncode != 0:
        print(f"Error parsing {source_path}:", file=sys.stderr)
        print(result.stderr, file=sys.stderr)
        return None
    return result.stdout


def cmd_parse(args):
    """Parse a single RPG3 source file."""
    jar = find_jar()
    output = run_parser(jar, args.source, args.charset, args.copy_path)
    if output is None:
        sys.exit(1)

    if args.output:
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(output, encoding="utf-8")
        print(f"Written: {args.output}")
    else:
        print(output)


def cmd_batch(args):
    """Parse all RPG3 source files in a directory."""
    jar = find_jar()
    extensions = {".rpg", ".rpg3", ".rpgsrc", ".mbr"}
    source_files = [f for f in args.source_dir.rglob("*") if f.suffix.lower() in extensions]

    if not source_files:
        print(f"No RPG3 source files found in {args.source_dir}", file=sys.stderr)
        sys.exit(1)

    print(f"Found {len(source_files)} source files")
    output_dir = args.output_dir or args.source_dir / "ir_output"
    output_dir.mkdir(parents=True, exist_ok=True)

    def parse_one(source_file):
        output = run_parser(jar, source_file, args.charset, args.copy_path)
        if output:
            out_file = output_dir / (source_file.stem + ".json")
            out_file.write_text(output, encoding="utf-8")
            return source_file.name, True
        return source_file.name, False

    success = 0
    failed = 0
    with ThreadPoolExecutor(max_workers=args.parallel) as executor:
        futures = {executor.submit(parse_one, f): f for f in source_files}
        for future in as_completed(futures):
            name, ok = future.result()
            if ok:
                success += 1
                print(f"  ✓ {name}")
            else:
                failed += 1
                print(f"  ✗ {name}")

    print(f"\nDone: {success} succeeded, {failed} failed")


def cmd_validate(args):
    """Validate an IR JSON file."""
    try:
        content = args.json_file.read_text(encoding="utf-8")
        ir = json.loads(content)
    except json.JSONDecodeError as e:
        print(f"Invalid JSON: {e}", file=sys.stderr)
        sys.exit(1)

    errors = []

    # Check required top-level keys
    for key in ("metadata", "content", "dependencies"):
        if key not in ir:
            errors.append(f"Missing top-level key: '{key}'")

    # Check metadata
    if "metadata" in ir and ir["metadata"]:
        meta = ir["metadata"]
        for field in ("irVersion", "sourceType", "parseInfo"):
            if field not in meta:
                errors.append(f"Missing metadata.{field}")

    # Check content type
    if "content" in ir and ir["content"]:
        content_obj = ir["content"]
        expected_arrays = ["fileSpecs", "calculationSpecs", "symbolTable"]
        for arr in expected_arrays:
            if arr in content_obj and not isinstance(content_obj[arr], list):
                errors.append(f"content.{arr} should be an array")

    if errors:
        print(f"Validation FAILED ({len(errors)} issues):")
        for e in errors:
            print(f"  • {e}")
        sys.exit(1)
    else:
        meta = ir.get("metadata", {})
        print(f"Validation PASSED")
        print(f"  IR Version: {meta.get('irVersion', 'N/A')}")
        print(f"  Source Type: {meta.get('sourceType', 'N/A')}")
        print(f"  Source Member: {meta.get('sourceMember', 'N/A')}")
        parse_info = meta.get("parseInfo", {})
        print(f"  Parse Status: {parse_info.get('parseStatus', 'N/A')}")
        print(f"  Total Lines: {parse_info.get('totalLines', 'N/A')}")


def main():
    parser = argparse.ArgumentParser(
        prog="rpg3-parser",
        description="RPG3 Parser CLI — Parse AS/400 RPG III source files to IR JSON"
    )
    subparsers = parser.add_subparsers(dest="command", help="Available commands")

    # parse
    parse_p = subparsers.add_parser("parse", help="Parse a single RPG3 source file")
    parse_p.add_argument("source", type=Path, help="Path to RPG3 source file")
    parse_p.add_argument("-o", "--output", type=Path, help="Output JSON file path")
    parse_p.add_argument("--copy-path", type=str, help="Colon/semicolon-separated copy paths")
    parse_p.add_argument("--charset", default="UTF-8", help="Source file charset (default: UTF-8)")
    parse_p.set_defaults(func=cmd_parse)

    # batch
    batch_p = subparsers.add_parser("batch", help="Parse all RPG3 files in a directory")
    batch_p.add_argument("source_dir", type=Path, help="Directory containing RPG3 source files")
    batch_p.add_argument("-o", "--output-dir", type=Path, help="Output directory for JSON files")
    batch_p.add_argument("--copy-path", type=str, help="Colon/semicolon-separated copy paths")
    batch_p.add_argument("--charset", default="UTF-8", help="Source file charset (default: UTF-8)")
    batch_p.add_argument("--parallel", type=int, default=4, help="Number of parallel workers (default: 4)")
    batch_p.set_defaults(func=cmd_batch)

    # validate
    validate_p = subparsers.add_parser("validate", help="Validate an IR JSON file")
    validate_p.add_argument("json_file", type=Path, help="Path to IR JSON file")
    validate_p.set_defaults(func=cmd_validate)

    args = parser.parse_args()
    if not args.command:
        parser.print_help()
        sys.exit(1)

    args.func(args)


if __name__ == "__main__":
    main()
