# MySQL Setup

The application uses MySQL by default.

Default connection values:

```properties
DB_HOST=localhost
DB_PORT=3306
DB_NAME=lodgings_restaurant_db
DB_USERNAME=root
DB_PASSWORD=12345
```

Start the app from PowerShell:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="12345"
.\gradlew.bat bootRun
```

The database is created automatically because the JDBC URL includes `createDatabaseIfNotExist=true`.
