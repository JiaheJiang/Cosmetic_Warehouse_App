## Cosmetic Warehouse Management Application

### Overview
This project is a desktop application for managing cosmetic products in a warehouse. Users can view, add, remove, and purchase products, and save/load the warehouse state. The backend is implemented in Java with local storage in JSON format; two frontends are provided — a console UI and a Java Swing GUI — both backed by the same model and persistence layer.

The application code lives in [`project_z4p0g/`](project_z4p0g/).

### Features
- **View Products**: Display all cosmetic products currently in the warehouse.
- **Add Products**: Add new cosmetic products to the warehouse inventory.
- **Remove Products**: Remove existing products (the warehouse always keeps at least one product).
- **Purchase Products** (GUI): Record purchases of the selected product.
- **Match Products** (console): Match a customer's needed product type to an available brand.
- **Save / Load State**: Persist the warehouse to `data/warehouse.json` and reload it on startup.
- **Event Log**: All add/remove/purchase/save/load actions are logged and printed when the GUI closes.

### Project Structure
```
project_z4p0g/
├── src/main/
│   ├── model/         Warehouse, Cosmetic, Event, EventLog + exceptions
│   ├── persistence/   JSON reader/writer (org.json)
│   └── ui/            Main + CosmeticApp (console), CosmeticAppGUI + WarehouseGUI (Swing)
├── src/test/          JUnit 5 tests for model and persistence
├── data/              JSON store and test fixtures
└── lib/               Jars for IDE-based setups (Maven users don't need these)
```

### Build and Test
Requires JDK 11+ and Maven. From `project_z4p0g/`:

```bash
mvn test                                          # run the test suite
mvn compile exec:java                             # run the console app
mvn compile exec:java -Dexec.mainClass=ui.CosmeticAppGUI   # run the Swing GUI
```

The project can also be opened directly in IntelliJ IDEA using the bundled jars in `lib/`.

### Usage
- **Console app** (`ui.Main`): menu-driven — view, add, remove, order, save, and load.
- **GUI app** (`ui.CosmeticAppGUI`): log in (username `Crystal`, password `666666` — a demo login, not real security), choose whether to load the last saved state, then manage products in the warehouse window. The combo box on the right shows the current products; the text field on the left adds a new product by type.

### Contributing
Contributions are welcome! Please fork the repository and submit a pull request with your changes.
