# Clubber-Backend

## Project Structure

### Main.java

**DO NOT MODIFY**

### Controllers

Connection/API request handlers. Grouped by users, organizations .etc. For example, endpoint handlers for `GET users/:userId` and `PUT users/:userId` will both be in `UserController.java`.

**Naming:** `<Endpoint>Controller.java` -> `OrganizationController.java`

#### Responses

Response schema providers for Controllers, also classified with individual endpoint groups.

**Naming:** `<HTTPMethod>.java` -> `Responses/Users/Get.java`

### Managers

Database accessors for corresponding collections: `user`, `organization` .etc. Managers **DO NOT** mutate data directly.

**Naming:** `<CollectionName>Manager.java` -> `UserManager.java`

### Processors

Data mutator/processor. Add if needed. Processors **DO NOT** read/save data directly.

**Naming:** `<ModuleName>Processor.java` -> `MessageProcessor.java`

### Lib(rary)

Under `group7/lib`. Use when modules may be referenced by multiple segments of code (`utilities`) or as type providers (`types`)

## Style Guide

- Class Names: PascalCase
- Variable Names: camelCase
- Method Names: camelCase
- Constant Names: ALL_CAPS/CONSTANT_CASE

All Managers and Processors should be written in **[Singleton Pattern](https://en.wikipedia.org/wiki/Singleton_pattern)**

IDs for different collections should be extended from `String` class as peer classes to avoid referencing confusion.