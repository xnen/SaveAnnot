# SaveAnnot
Java Annotation utilizing GSON to conveniently 'save' field values utilized in a user interface

Used by 
```
@Save(visible={visible_in_ui, true/false}, displayName="{ui_name_reference}", min={min_value}, max={max_value}) above any supported field.
```

All properties can be read/written through `getUIBridge()` in Settings.java, `setValue`s will automatically populate each field with the passed Object.

Known Issues:
- Unsupported field types
- Numeric field type mismatches (Integers may display as floating points in user interfaces)
