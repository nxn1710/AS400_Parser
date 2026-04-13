#!/usr/bin/env python3
"""
AS400 Source Parser CLI — Python wrapper for the Java parser library.

Supports RPGLE (.rpgle, .rpgleinc),
RPG3 (.rpg, .rpg3, .mbr, .cpy),
DDS (.pf, .lf, .dspf, .prtf), and CL (.cl, .clp, .clle).
Parser is auto-detected from file extension.

Usage:
    as400-parser parse SOURCE [-o OUTPUT] [--copy-path PATHS] [--charset CHARSET]
    as400-parser batch SOURCE_DIR [-o OUTPUT_DIR] [--copy-path PATHS]
    as400-parser validate JSON_FILE
"""

import argparse
import json
import subprocess
import sys
import io
from pathlib import Path
from concurrent.futures import ThreadPoolExecutor, as_completed

# Force UTF-8 encoding for stdout/stderr on Windows to support symbols
if sys.platform == "win32":
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8")
    sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding="utf-8")

# Default JAR location (relative to this script)
DEFAULT_JAR = Path(__file__).parent.parent / "parser-core" / "build" / "libs" / "as400-parser-core-1.0.0-SNAPSHOT-all.jar"

# All supported extensions across all parsers
SUPPORTED_EXTENSIONS = {
    ".rpgle", ".rpgleinc",                                  # RPGLE
    ".rpg", ".rpg3", ".rpgsrc", ".mbr", ".cpy", ".cpysrc",  # RPG3
    ".pf", ".lf",                                             # DDS (PF/LF)
    ".dspf",                                                  # DSPF
    ".prtf",                                                  # PRTF
    ".cl", ".clp", ".clle",                                   # CL
}


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
        "java",
        "-Dfile.encoding=UTF-8",
        "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8",
        "-jar", str(jar_path),
        "--source", str(source_path),
        "--charset", charset,
    ]
    if copy_path:
        cmd.extend(["--copy-path", copy_path])

    result = subprocess.run(cmd, capture_output=True, text=True, timeout=60, encoding="utf-8")
    if result.returncode != 0:
        print(f"Error parsing {source_path}:", file=sys.stderr)
        print(result.stderr, file=sys.stderr)
        return None
    return result.stdout


def cmd_parse(args):
    """Parse a single supported source file."""
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
    """Parse all supported source files in a directory using Java batch mode.
    
    Uses Java --source-dir mode to enable REFFLD cross-file resolution.
    """
    jar = find_jar()
    
    # Quick check for files before invoking Java
    source_files = [f for f in args.source_dir.rglob("*") if f.suffix.lower() in SUPPORTED_EXTENSIONS]
    if not source_files:
        print(f"No source files found in {args.source_dir}", file=sys.stderr)
        sys.exit(1)

    rpgle_count = sum(1 for f in source_files if f.suffix.lower() in {".rpgle", ".rpgleinc"})
    rpg_count = sum(1 for f in source_files if f.suffix.lower() in {".rpg", ".rpg3", ".rpgsrc", ".mbr", ".cpy", ".cpysrc"})
    dds_count = sum(1 for f in source_files if f.suffix.lower() in {".pf", ".lf"})
    dspf_count = sum(1 for f in source_files if f.suffix.lower() == ".dspf")
    prtf_count = sum(1 for f in source_files if f.suffix.lower() == ".prtf")
    cl_count = sum(1 for f in source_files if f.suffix.lower() in {".cl", ".clp", ".clle"})

    parts = []
    if rpgle_count: parts.append(f"RPGLE: {rpgle_count}")
    if rpg_count: parts.append(f"RPG3: {rpg_count}")
    if dds_count: parts.append(f"DDS: {dds_count}")
    if dspf_count: parts.append(f"DSPF: {dspf_count}")
    if prtf_count: parts.append(f"PRTF: {prtf_count}")
    if cl_count: parts.append(f"CL: {cl_count}")
    print(f"Found {len(source_files)} source files ({', '.join(parts)})")

    output_dir = args.output_dir or args.source_dir / "ir_output"

    # Delegate to Java batch mode (enables REFFLD cross-file resolution)
    cmd = [
        "java",
        "-Dfile.encoding=UTF-8",
        "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8",
        "-jar", str(jar),
        "--source-dir", str(args.source_dir),
        "--output-dir", str(output_dir),
        "--charset", args.charset,
    ]
    if args.analyze:
        cmd.append("--analyze")
    if args.copy_path:
        cmd.extend(["--copy-path", args.copy_path])

    result = subprocess.run(cmd, capture_output=True, text=True, timeout=300, encoding="utf-8")
    
    # Print Java's output (progress messages go to stderr)
    # Skip the "Found N source files" line since we already printed it
    if result.stderr:
        for line in result.stderr.strip().split("\n"):
            if line.startswith("Found "):
                continue
            print(line)
    if result.stdout:
        print(result.stdout, end="")
    
    if result.returncode != 0:
        sys.exit(1)


def cmd_analyze(args):
    """Run analysis on existing IR JSON files."""
    jar = find_jar()

    print(f"Analyzing IR files in {args.ir_dir}...")
    output_dir = args.output_dir or args.ir_dir

    cmd = [
        "java",
        "-Dfile.encoding=UTF-8",
        "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8",
        "-jar", str(jar),
        "--ir-dir", str(args.ir_dir),
        "--output-dir", str(output_dir),
    ]

    result = subprocess.run(cmd, capture_output=True, text=True, timeout=300, encoding="utf-8")

    if result.stderr:
        print(result.stderr)
    if result.stdout:
        print(result.stdout, end="")

    if result.returncode != 0:
        sys.exit(1)


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
        prog="as400-parser",
        description="AS400 Source Parser CLI — Parse RPGLE, RPG3, DDS, and CL source files to IR JSON"
    )
    subparsers = parser.add_subparsers(dest="command", help="Available commands")

    # parse
    parse_p = subparsers.add_parser("parse", help="Parse a single source file")
    parse_p.add_argument("source", type=Path, help="Path to source file (.rpg, .pf, .cl, etc.)")
    parse_p.add_argument("-o", "--output", type=Path, help="Output JSON file path")
    parse_p.add_argument("--copy-path", type=str, help="Colon/semicolon-separated copy paths (RPG3 only)")
    parse_p.add_argument("--charset", default="auto", help="Source file charset (default: auto-detect)")
    parse_p.set_defaults(func=cmd_parse)

    # batch
    batch_p = subparsers.add_parser("batch", help="Parse all source files in a directory")
    batch_p.add_argument("source_dir", type=Path, help="Directory containing source files")
    batch_p.add_argument("-o", "--output-dir", type=Path, help="Output directory for JSON files")
    batch_p.add_argument("--analyze", action="store_true", help="Execute relational and cross-reference analysis after batch")
    batch_p.add_argument("--copy-path", type=str, help="Colon/semicolon-separated copy paths (RPG3 only)")
    batch_p.add_argument("--charset", default="auto", help="Source file charset (default: auto-detect)")
    batch_p.set_defaults(func=cmd_batch)

    # validate
    validate_p = subparsers.add_parser("validate", help="Validate an IR JSON file")
    validate_p.add_argument("json_file", type=Path, help="Path to IR JSON file")
    validate_p.set_defaults(func=cmd_validate)

    # analyze
    analyze_p = subparsers.add_parser("analyze", help="Run analysis on existing IR JSON files")
    analyze_p.add_argument("ir_dir", type=Path, help="Directory containing IR JSON files")
    analyze_p.add_argument("-o", "--output-dir", type=Path, help="Output directory for analysis result")
    analyze_p.set_defaults(func=cmd_analyze)

    args = parser.parse_args()
    if not args.command:
        parser.print_help()
        sys.exit(1)

    args.func(args)


if __name__ == "__main__":
    main()
