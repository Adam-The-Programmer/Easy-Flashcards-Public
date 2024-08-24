-- phpMyAdmin SQL Dump
-- version 4.9.7
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Czas generowania: 24 Sie 2024, 20:12
-- Wersja serwera: 10.4.28-MariaDB-cll-lve
-- Wersja PHP: 7.2.34

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Baza danych: `lbiio_easyflashcards`
--

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `achievement`
--

CREATE TABLE `achievement` (
  `AwardCode` int(2) NOT NULL,
  `PointsToEarn` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;



-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `flashcard`
--

CREATE TABLE `flashcard` (
  `FlashcardID` varchar(200) NOT NULL,
  `Word` varchar(100) NOT NULL,
  `Translations` varchar(500) NOT NULL,
  `Explanations` varchar(1000) NOT NULL,
  `Phrases` varchar(1000) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `flashcardtopackage`
--

CREATE TABLE `flashcardtopackage` (
  `FlashcardIDForeign` varchar(200) NOT NULL,
  `PackageIDForeign` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `learningcardprogress`
--

CREATE TABLE `learningcardprogress` (
  `FlashcardIDForeign` varchar(200) NOT NULL,
  `UIDForeign` varchar(100) NOT NULL,
  `learningTranslationKnowledgeLevel` int(1) NOT NULL,
  `learningExplanationKnowledgeLevel` int(1) NOT NULL,
  `learningPhraseKnowledgeLevel` int(1) NOT NULL,
  `isLearningTranslationKnown` tinyint(1) NOT NULL,
  `isLearningExplanationKnown` tinyint(1) NOT NULL,
  `isLearningPhraseKnown` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `learningpackageprogress`
--

CREATE TABLE `learningpackageprogress` (
  `UIDForeign` varchar(100) NOT NULL,
  `PackageIDForeign` varchar(200) NOT NULL,
  `learningTranslationsBestScore` int(11) NOT NULL,
  `learningExplanationsBestScore` int(11) NOT NULL,
  `learningPhrasesBestScore` int(11) NOT NULL,
  `learningTranslationsCurrentScore` int(11) NOT NULL,
  `learningExplanationsCurrentScore` int(11) NOT NULL,
  `learningPhrasesCurrentScore` int(11) NOT NULL,
  `areLearningTranslationsCompleted` tinyint(1) NOT NULL,
  `areLearningExplanationsCompleted` tinyint(1) NOT NULL,
  `areLearningPhrasesCompleted` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `memorypackageprogress`
--

CREATE TABLE `memorypackageprogress` (
  `UIDForeign` varchar(100) NOT NULL,
  `PackageIDForeign` varchar(200) NOT NULL,
  `memoryBestScore` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `package`
--

CREATE TABLE `package` (
  `PackageID` varchar(200) NOT NULL,
  `Name` varchar(50) NOT NULL,
  `Path` varchar(400) NOT NULL,
  `BackLanguage` varchar(50) NOT NULL,
  `FrontLanguage` varchar(50) NOT NULL,
  `maxKnowledgeLevel` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `packagetobuy`
--

CREATE TABLE `packagetobuy` (
  `PackageIDForeign` varchar(200) NOT NULL,
  `Description` varchar(500) NOT NULL,
  `Downloads` int(11) NOT NULL,
  `Price` int(11) NOT NULL,
  `Currency` varchar(3) NOT NULL,
  `MaxPoints` int(11) NOT NULL,
  `AcquiredPoints` int(11) NOT NULL,
  `LastPackageChange` bigint(20) NOT NULL,
  `ChangedCards` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `quizpackageprogress`
--

CREATE TABLE `quizpackageprogress` (
  `UIDForeign` varchar(100) NOT NULL,
  `PackageIDForeign` varchar(200) NOT NULL,
  `quizTranslationsBestScore` int(11) NOT NULL,
  `quizExplanationsBestScore` int(11) NOT NULL,
  `quizPhrasesBestScore` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `user`
--

CREATE TABLE `user` (
  `UID` varchar(100) NOT NULL,
  `Email` varchar(50) NOT NULL,
  `Phone` varchar(20) NOT NULL,
  `Country` varchar(50) NOT NULL,
  `AllPoints` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `usertoachievement`
--

CREATE TABLE `usertoachievement` (
  `UserToAchievementID` int(11) NOT NULL,
  `UIDForeign` varchar(100) NOT NULL,
  `AwardCodeForeign` int(11) NOT NULL,
  `description` varchar(200) NOT NULL,
  `achievementTimestamp` bigint(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `usertoflashcard`
--

CREATE TABLE `usertoflashcard` (
  `UIDForeign` varchar(100) NOT NULL,
  `FlashcardIDForeign` varchar(200) NOT NULL,
  `isImportant` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `usertopackage`
--

CREATE TABLE `usertopackage` (
  `UIDForeign` varchar(100) NOT NULL,
  `PackageIDForeign` varchar(200) NOT NULL,
  `State` int(1) NOT NULL,
  `Points` int(11) NOT NULL,
  `rate` int(1) NOT NULL,
  `LastPackageChange` bigint(20) NOT NULL,
  `ChangedCards` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


--
-- Indeksy dla zrzutów tabel
--

--
-- Indeksy dla tabeli `achievement`
--
ALTER TABLE `achievement`
  ADD PRIMARY KEY (`AwardCode`);

--
-- Indeksy dla tabeli `flashcard`
--
ALTER TABLE `flashcard`
  ADD PRIMARY KEY (`FlashcardID`);

--
-- Indeksy dla tabeli `flashcardtopackage`
--
ALTER TABLE `flashcardtopackage`
  ADD KEY `FlashcardIDForeign` (`FlashcardIDForeign`),
  ADD KEY `PackageIDForeign` (`PackageIDForeign`);

--
-- Indeksy dla tabeli `learningcardprogress`
--
ALTER TABLE `learningcardprogress`
  ADD KEY `FlashcardIDForeign` (`FlashcardIDForeign`),
  ADD KEY `UIDForeign` (`UIDForeign`);

--
-- Indeksy dla tabeli `learningpackageprogress`
--
ALTER TABLE `learningpackageprogress`
  ADD KEY `UIDForeign` (`UIDForeign`),
  ADD KEY `PackageIDForeign` (`PackageIDForeign`);

--
-- Indeksy dla tabeli `memorypackageprogress`
--
ALTER TABLE `memorypackageprogress`
  ADD KEY `UIDForeign` (`UIDForeign`),
  ADD KEY `PackageIDForeign` (`PackageIDForeign`);

--
-- Indeksy dla tabeli `package`
--
ALTER TABLE `package`
  ADD PRIMARY KEY (`PackageID`);

--
-- Indeksy dla tabeli `packagetobuy`
--
ALTER TABLE `packagetobuy`
  ADD KEY `PackageIDForeign` (`PackageIDForeign`);

--
-- Indeksy dla tabeli `quizpackageprogress`
--
ALTER TABLE `quizpackageprogress`
  ADD KEY `UIDForeign` (`UIDForeign`),
  ADD KEY `PackageIDForeign` (`PackageIDForeign`);

--
-- Indeksy dla tabeli `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`UID`);

--
-- Indeksy dla tabeli `usertoachievement`
--
ALTER TABLE `usertoachievement`
  ADD PRIMARY KEY (`UserToAchievementID`),
  ADD KEY `UIDForeign` (`UIDForeign`),
  ADD KEY `AchivementIDForeign` (`AwardCodeForeign`);

--
-- Indeksy dla tabeli `usertoflashcard`
--
ALTER TABLE `usertoflashcard`
  ADD KEY `UIDForeign` (`UIDForeign`),
  ADD KEY `FlashcardIDForeign` (`FlashcardIDForeign`);

--
-- Indeksy dla tabeli `usertopackage`
--
ALTER TABLE `usertopackage`
  ADD KEY `UIDForeign` (`UIDForeign`),
  ADD KEY `PackageIDForeign` (`PackageIDForeign`),
  ADD KEY `UIDForeign_2` (`UIDForeign`);

--
-- AUTO_INCREMENT dla zrzuconych tabel
--

--
-- AUTO_INCREMENT dla tabeli `achievement`
--
ALTER TABLE `achievement`
  MODIFY `AwardCode` int(2) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT dla tabeli `usertoachievement`
--
ALTER TABLE `usertoachievement`
  MODIFY `UserToAchievementID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- Ograniczenia dla zrzutów tabel
--

--
-- Ograniczenia dla tabeli `flashcardtopackage`
--
ALTER TABLE `flashcardtopackage`
  ADD CONSTRAINT `flashcardtopackage_ibfk_1` FOREIGN KEY (`FlashcardIDForeign`) REFERENCES `flashcard` (`FlashcardID`),
  ADD CONSTRAINT `flashcardtopackage_ibfk_2` FOREIGN KEY (`PackageIDForeign`) REFERENCES `package` (`PackageID`);

--
-- Ograniczenia dla tabeli `learningcardprogress`
--
ALTER TABLE `learningcardprogress`
  ADD CONSTRAINT `learningcardprogress_ibfk_1` FOREIGN KEY (`UIDForeign`) REFERENCES `user` (`UID`),
  ADD CONSTRAINT `learningcardprogress_ibfk_2` FOREIGN KEY (`FlashcardIDForeign`) REFERENCES `flashcard` (`FlashcardID`);

--
-- Ograniczenia dla tabeli `learningpackageprogress`
--
ALTER TABLE `learningpackageprogress`
  ADD CONSTRAINT `learningpackageprogress_ibfk_1` FOREIGN KEY (`UIDForeign`) REFERENCES `user` (`UID`),
  ADD CONSTRAINT `learningpackageprogress_ibfk_2` FOREIGN KEY (`PackageIDForeign`) REFERENCES `package` (`PackageID`);

--
-- Ograniczenia dla tabeli `memorypackageprogress`
--
ALTER TABLE `memorypackageprogress`
  ADD CONSTRAINT `memorypackageprogress_ibfk_1` FOREIGN KEY (`UIDForeign`) REFERENCES `user` (`UID`),
  ADD CONSTRAINT `memorypackageprogress_ibfk_2` FOREIGN KEY (`PackageIDForeign`) REFERENCES `package` (`PackageID`);

--
-- Ograniczenia dla tabeli `packagetobuy`
--
ALTER TABLE `packagetobuy`
  ADD CONSTRAINT `packagetobuy_ibfk_1` FOREIGN KEY (`PackageIDForeign`) REFERENCES `package` (`PackageID`);

--
-- Ograniczenia dla tabeli `quizpackageprogress`
--
ALTER TABLE `quizpackageprogress`
  ADD CONSTRAINT `quizpackageprogress_ibfk_1` FOREIGN KEY (`UIDForeign`) REFERENCES `user` (`UID`),
  ADD CONSTRAINT `quizpackageprogress_ibfk_2` FOREIGN KEY (`PackageIDForeign`) REFERENCES `package` (`PackageID`);

--
-- Ograniczenia dla tabeli `usertoachievement`
--
ALTER TABLE `usertoachievement`
  ADD CONSTRAINT `usertoachievement_ibfk_1` FOREIGN KEY (`UIDForeign`) REFERENCES `user` (`UID`),
  ADD CONSTRAINT `usertoachievement_ibfk_2` FOREIGN KEY (`AwardCodeForeign`) REFERENCES `achievement` (`AwardCode`);

--
-- Ograniczenia dla tabeli `usertoflashcard`
--
ALTER TABLE `usertoflashcard`
  ADD CONSTRAINT `usertoflashcard_ibfk_1` FOREIGN KEY (`UIDForeign`) REFERENCES `user` (`UID`),
  ADD CONSTRAINT `usertoflashcard_ibfk_2` FOREIGN KEY (`FlashcardIDForeign`) REFERENCES `flashcard` (`FlashcardID`);

--
-- Ograniczenia dla tabeli `usertopackage`
--
ALTER TABLE `usertopackage`
  ADD CONSTRAINT `usertopackage_ibfk_1` FOREIGN KEY (`UIDForeign`) REFERENCES `user` (`UID`),
  ADD CONSTRAINT `usertopackage_ibfk_2` FOREIGN KEY (`PackageIDForeign`) REFERENCES `package` (`PackageID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
