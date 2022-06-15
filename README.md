# Android Strings
Contains tools to convert android resource strings from xml to csv and vice-versa.
Can be used to provide human translators the strings to translate in a readable format and then convert it back to xml to add it to the app.

## Usage
1. Use `strings-to-csv` to export the app's strings from xml to csv(s). Export one csv file for each language
2. Send the csv files to human translators where they can input the translations
3. Use `csv-to-strings` to convert the csv files back to xml
4. Import the xmls into your app

## strings-to-csv
Convert android resource strings from xml to csv.

### Usage
```
./gradlew run --args="-dir /home/minas/Projects/myproject"
```

The tools recursively scans source folders, finds the relevant string resource files and converts them to csv.

### Output
A directory with the converted files in csv format.

## csv-to-strings
Convert csv files to xml for using in android apps.

### Usage
```
./gradlew run --args="--originalFile C:\Users\m.mina\Projects\myproject\src\main\res\values\strings.xml --localizedFile C:\Users\m.mina\Projects\myproject\src\main\res\values-de\strings.xml --userTranslations C:\Users\m.mina\Downloads\translated_strings.csv"
```

| Argument | Explanation |
| -------- | ----------- |
| originalFile | The `strings.xml` that contains the strings in the app's default language. |
| localizedFile | `The strings.xml` that contains the strings for the language you want to import translation for. |
| userTranslations | The `csv` file that was provided by the translator. |

Note: Unlike `strings-to-csv`, `csv-to-strings` converts files one  by one.
