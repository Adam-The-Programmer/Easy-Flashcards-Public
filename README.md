# Easy Flashcards 
Easy Flashcards App revolutionizes language learning with a modern approach, focusing on learning words in context. Create your own flashcard packages and share them, or download packages shared by others. Enhance your language skills and rise to the top of our leaderboard as you learn!

# <img src="https://cdn-icons-png.flaticon.com/512/3430/3430793.png" width="30" height="30"> Idea
Easy Flashcards offers an innovative, phrase-based approach to language learning, ideal for enhancing fluency. With the flexibility to create flashcards featuring a single word, multiple translations, detailed explanations, and practical usage examples, this app tailors the learning experience to your needs. Navigate through different learning modes—exploration or targeted learning—for each element: word-to-translations, word-to-explanations, and word-to-phrases. Engage with our interactive Quiz game to receive random cards from a selected package, or delight in the challenge of our memory game, discovering pairs and tracking your progress across various combinations.
The application also supports collaborative learning by allowing you to share your own flashcard packages and download those created by others. Ascend the leaderboard for each package or compete globally. In the event of device loss, our advanced data integrity mechanisms ensure that you never lose a package or flashcard. Simply log in on a new device, and your progress is instantly restored. Plus, receive immediate updates whenever a teacher modifies a package, keeping your learning materials current with Easy Flashcards’ real-time notifications.

> [!TIP]
> Watch the film below
[![YouTube](http://i.ytimg.com/vi/61pNrGnnFuc/hqdefault.jpg)](https://www.youtube.com/watch?v=61pNrGnnFuc)

[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/61pNrGnnFuc/0.jpg)](https://www.youtube.com/watch?v=61pNrGnnFuc)

# :sparkles: Features List
1. Creating, Modifying and Removing Packages & Flashcards - Relational DB
2. Creating Cards containing one word to many translations, explanations phrases
3. Playing Explanation Game - just browsing cards
4. Playing Learning Game - aplication gets 5 random cards and displays them in random order. You have to decide weather they know then or not. If You clicks know on 3 cards and dont know on 2, aplication removes these 2 known from the list and gets another 2 random generated to have always group of 5 cards - **this feature is designed to enhancce the proces of memorization**. You have to decide know or don't quickly because time is running out...
5. Quiz Game - if you have package of 100 cards quiz will get random 15. In this mode you earn the more points the more time is left
6. Memory Game - The ordinary game popular with children and people who want to improve their memorization skills
7. Sharing & Downloading Package - as you select share from dropdown menu and fill sharing form your package will be visible for all other users allowing them to downloading it.
8. **Data Integrity** - Every action of user is registered and is automatically respected by RoomDB, but what about CloudDB? Thanks to **Data Integrity Mechanism** the internet connection is no longer a factor you have to worry about. Background Service constantly analyzes internet availability by pinging Google DNS Server, and **it allows API endpoint to be called only if there is certainity that internet connection is good enough**. **Data Integrity Mechanism** also ensurses you that **whenever anyone changes something in their shared package, people who downloaded it obtain this change at once!**


# :hourglass_flowing_sand: Coming Soon
1. Implementation of <img src="https://www.sensepass.com/wp-content/uploads/2023/06/paypal-payment-icon-editorial-logo-free-vector.jpeg" style="vertical-align: middle; width: 70px; height: 50px;"> PayPal Service to allow users to monetize their packages

# <img src="https://cdn-icons-png.flaticon.com/512/9243/9243391.png" width="30" height="30"> Enabling Database
import easyflashcardsdatabase.sql file to your phpMyAdmin
![image](https://github.com/user-attachments/assets/7e4dcbab-2053-4178-9309-e8bb373029f5)


# <img src="https://www.gstatic.com/devrel-devsite/prod/ve6d23e3d09b80ebb8aa912b18630ed278e1629b97aee6522ea53593a0024d951/firebase/images/touchicon-180.png" width="30" height="30"> Configure Firebase
Enable:
1. Firebase Authentication by Email/Passowrd with functionality of verification by email
2. Firestore Database. Start 1 collection: *users* 
3. Storage wit root folder *image*

# <img src="https://cdn-icons-png.flaticon.com/512/4380/4380600.png" width="30" height="30"> Run API Server
1. Create google cloud account
2. Set up new project "Easy Flashcards API Server" and enter terminal
3. Use the following bash commands to turn server on:
```
# Download and unzip spring files
curl -o easyflashcardsapiserver.zip http://test.lbiio.pl/easyflashcardsapiserver.zip
unzip easyflashcardsapiserver.zip

# Select direcctory
cd easyflashcardsapiserver

# Add executive privileges for maven file
chmod +x mvnw

# Clean & Run Spring App
./mvnw clean
./mvnw spring-boot:run

# Deploy
gcloud app deploy

```
> [!TIP]
> if there is an error informing that 8080 port is already not available,</br>
> use the following to kill all processes associated with project:
> 
> ```kill -9 -1```

> [!WARNING]
> Remember to configure properties.xml file to access database correctly
>
> 
# <img src="https://techcrunch.com/wp-content/uploads/2020/10/image9.png" width="30" height="30"> Runing Android Studio
> [!CAUTION]
> 1. Replace google-services.json with your file generated by firabse!
> 2. In ApiModule.kt change path to your api server (you will obtain it as you invoke gcloud app deploy)

