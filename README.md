
A demo movie android app that demonstrates Clean Architecture and is written in Kotlin.💯🎞 

# Features

1. **Offline-first**: The app can be accessed even without an internet connection.
2. **Pagination**: Efficiently loads large amounts of data to improve the user experience.
3. **Search functionality**: Allows users to quickly find specific information within the app.
4. **Auto Sync**: Uses both NetworkConnectivityStream and WorkManager to ensure data is always up-to-date.
5. **Favorites**: Users can add movies to a favorites list.



### The Motivation behind the app
This repository was created with the intention of stepping outside of my comfort zone as much as possible, tackling topics outside of my area of expertise, and using it to implement new challenges and ideas.

### ⚠️NOTE
Architecture by its nature is **dynamic** and **ever-evolving**, there are always several solutions to every problem, and what works best will depend on the specific requirements and constraints of your project.



# Clean Architecture

The core principles of the clean approach can be summarized as followed:

#### 1. The application code is separated into layers.

These layers define the separation of concerns inside the code base.

#### 2. The layers follow a strict dependency rule.

Each layer can only interact with the layers below it.

#### 3. As we move toward the bottom layer — the code becomes generic.

The bottom layers dictate policies and rules, and the upper layers dictate implementation details such as the database, networking manager, and UI.

# Architecture Layers

The application consists of three layers:

The domain layer, the data layer, and the presentation layer.

Looking at project’s high-level structure, you’ll see that each layer is represented by a module in the project.


I like it because it helps me avoid accidentals “leaks” between the layers.
