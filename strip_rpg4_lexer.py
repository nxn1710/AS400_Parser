#!/usr/bin/env python3
"""Strip RPG IV constructs from Rpg3Lexer.g4 - Fixed version"""

INPUT = r"d:\Code\AS400_Parser\grammar\rpg3\Rpg3Lexer.g4"
OUTPUT = r"d:\Code\AS400_Parser\grammar\rpg3\Rpg3Lexer.g4.new"

with open(INPUT, 'r', encoding='utf-8') as f:
    content = f.read()

lines = content.split('\n')
keep = [True] * len(lines)

def find_mode_boundaries():
    """Find all mode boundaries: list of (mode_name, start_line, end_line)."""
    modes = []
    current_mode = None
    current_start = None
    for i, line in enumerate(lines):
        stripped = line.strip()
        # Match "mode XYZ;" with possible trailing comment
        if stripped.startswith('mode ') and ';' in stripped:
            mode_name = stripped[5:stripped.index(';')].strip()
            if current_mode is not None:
                modes.append((current_mode, current_start, i - 1))
            current_mode = mode_name
            current_start = i
    if current_mode is not None:
        modes.append((current_mode, current_start, len(lines) - 1))
    return modes

modes = find_mode_boundaries()
print("Found modes:")
for name, start, end in modes:
    print(f"  {name}: lines {start+1}-{end+1} ({end-start+1} lines)")

def remove_mode(mode_name):
    """Remove an entire mode block."""
    for name, start, end in modes:
        if name == mode_name:
            print(f"REMOVING mode {mode_name}: lines {start+1}-{end+1}")
            for i in range(start, end + 1):
                keep[i] = False
            return True
    print(f"WARNING: mode {mode_name} not found")
    return False

# Modes to completely remove (RPG IV only)
modes_to_remove = [
    'FREE_Start',
    'FREE',
    'FREE_ENDED',
    'CheckDuration',
    'NumberContinuation',
    'SQL_MODE',
    'FIXED_ProcedureSpec',
    'FIXED_DefSpec',
    'CONTINUATION_ELIPSIS',
    'CONTINUATION_ELIPSIS_2',
    'FreeOpExtender',  # may not exist
]

print("\n--- Removing modes ---")
for mode_name in modes_to_remove:
    remove_mode(mode_name)

# === Clean up Selector mode ===
print("\n--- Cleaning Selector mode ---")
in_selector = False
for i, line in enumerate(lines):
    stripped = line.strip()
    if stripped == 'mode Selector;':
        in_selector = True
        continue
    if in_selector and stripped.startswith('mode ') and ';' in stripped:
        in_selector = False
        break
    if in_selector and keep[i]:
        # Remove: FREE_SPEC, DS_FIXED, PS_FIXED, CS_ExecSQL, COMMENTS
        if any(stripped.startswith(pref) for pref in ['FREE_SPEC', 'DS_FIXED', 'PS_FIXED', 'CS_ExecSQL', 'COMMENTS ']):
            keep[i] = False
            print(f"  Removing Selector line {i+1}: {stripped[:80]}")

# === Remove RPG IV-only opcode DEFINITIONS from FixedOpCodes ===
print("\n--- Cleaning FixedOpCodes mode ---")
rpg4_only_opcodes = {
    'OP_CALLP', 'OP_EVAL', 'OP_EVALR', 'OP_EVAL_CORR',
    'OP_FOR', 'OP_ENDFOR', 'OP_MONITOR', 'OP_ENDMON',
    'OP_ON_ERROR', 'OP_SELECT', 'OP_ENDSL', 'OP_OTHER',
    'OP_LEAVESR', 'OP_CALLB',
    'OP_ALLOC', 'OP_REALLOC', 'OP_DEALLOC',
    'OP_XML_INTO', 'OP_XML_SAX',
    'OP_WHEN',  # free-format WHEN (WHENxx is kept for fixed-format)
}

in_fixedopcodes = False
for i, line in enumerate(lines):
    stripped = line.strip()
    if 'mode FixedOpCodes' in stripped:
        in_fixedopcodes = True
        continue
    if in_fixedopcodes and stripped.startswith('mode ') and ';' in stripped:
        in_fixedopcodes = False
        break
    if in_fixedopcodes and keep[i]:
        for opcode in rpg4_only_opcodes:
            if stripped.startswith(opcode + ' ') or stripped.startswith(opcode + ':'):
                keep[i] = False
                print(f"  Removing opcode line {i+1}: {stripped[:60]}")
                # Remove following blank line
                if i + 1 < len(lines) and lines[i+1].strip() == '':
                    keep[i+1] = False
                break

# === Remove RPG IV-only CS_Operation_ rules from FIXED_CalcSpec ===
print("\n--- Cleaning FIXED_CalcSpec of RPG IV operations ---")
rpg4_cs_operations = {
    'CS_Operation_CALLP', 'CS_Operation_EVAL', 'CS_Operation_EVALR',
    'CS_Operation_EVAL_CORR', 'CS_Operation_FOR', 'CS_Operation_ENDFOR',
    'CS_Operation_MONITOR', 'CS_Operation_ENDMON', 'CS_Operation_ON_ERROR',
    'CS_Operation_SELECT', 'CS_Operation_ENDSL', 'CS_Operation_OTHER',
    'CS_Operation_LEAVESR', 'CS_Operation_CALLB',
    'CS_Operation_ALLOC', 'CS_Operation_REALLOC', 'CS_Operation_DEALLOC',
    'CS_Operation_XML_INTO', 'CS_Operation_XML_SAX',
    'CS_Operation_WHEN',
}

in_calc = False
for i, line in enumerate(lines):
    stripped = line.strip()
    if 'mode FIXED_CalcSpec;' in stripped or stripped == 'mode FIXED_CalcSpec;':
        in_calc = True
        continue
    if in_calc and stripped.startswith('mode ') and ';' in stripped:
        in_calc = False
        break
    if in_calc and keep[i]:
        for cs_op in rpg4_cs_operations:
            if stripped.startswith(cs_op):
                keep[i] = False
                print(f"  Removing CalcSpec line {i+1}: {stripped[:60]}")
                if i + 1 < len(lines) and lines[i+1].strip() == '':
                    keep[i+1] = False
                break

# === Remove EatCommentLines rules referencing FIXED_DefSpec ===
# These have semantic predicates checking _modeStack.contains(FIXED_DefSpec) which won't exist
print("\n--- Cleaning EatCommentLines referencing removed modes ---")
for i, line in enumerate(lines):
    if keep[i] and 'FIXED_DefSpec' in line:
        # Check if this is in a rule that OR's with other modes - we might need to just remove the predicate part
        print(f"  WARNING: FIXED_DefSpec reference at line {i+1}: {line.strip()[:80]}")

# === Remove references to free-format stuff in InStringMode ===
for i, line in enumerate(lines):
    if keep[i] and ('_modeStack.peek()==FREE' in line or '_modeStack.contains(FIXED_DefSpec)' in line):
        stripped = line.strip()
        # These are inside InStringMode / EatCommentLines - mode predicates that reference removed modes
        # We need to be careful here - we'll handle them case by case

# Build output
result_lines = []
for i, line in enumerate(lines):
    if keep[i]:
        result_lines.append(line)

result = '\n'.join(result_lines)

# Fix the grammar header comment
result = result.replace('Define a grammar called RpgLexer', 'Define a grammar called Rpg3Lexer')

with open(OUTPUT, 'w', encoding='utf-8') as f:
    f.write(result)

kept = sum(1 for k in keep if k)
removed = sum(1 for k in keep if not k)
print(f"\nTotal lines: {len(lines)}")
print(f"Kept: {kept}")
print(f"Removed: {removed}")
print(f"Output written to: {OUTPUT}")
