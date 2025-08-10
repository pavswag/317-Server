import zipfile, yaml

zip_path = 'drops.zip'
with zipfile.ZipFile(zip_path, 'r') as z:
    scores = []
    for name in z.namelist():
        if not name.endswith('.yml'):
            continue
        data = yaml.safe_load(z.read(name)) or {}
        score = 0
        for tier in ('very_rare', 'extremely_rare'):
            node = data.get(tier)
            if isinstance(node, dict):
                items = node.get('items')
                if isinstance(items, list):
                    score += len(items)
            elif isinstance(node, list):
                score += len(node)
        scores.append((score, name))
    scores.sort(reverse=True)
    for score, name in scores[:20]:
        print(f"{name}: {score}")
