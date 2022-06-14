import com.beust.jcommander.JCommander
import com.opencsv.CSVParserBuilder
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

private class AppTranslation(var line: String) {

    val name: String? get() {

        if (!line.contains("<string name=")
            || line.contains("translatable=\"false")) {
            return null
        }

        val nameStartIndex = line.indexOf("name=\"") + "name=\"".length
        val nameEndIndex = line.indexOf("\">")
        return line.substring(nameStartIndex, nameEndIndex)
    }

    var value: String?
        get() {

            if (!line.contains("<string name=")
                || line.contains("translatable=\"false")) {
                return null
            }

            val valueStartIndex = line.indexOf("\">") + 2
            val valueEndIndex = line.lastIndexOf("</string>")
            return line.substring(valueStartIndex, valueEndIndex)
        }
        set(newValue) {

            val value = value
            if (value != null && newValue != null) {
                // Append "</string>" so that if the key is the same as the value we won't replace that.
                line = line.replace("$value</string>", "$newValue</string>")
            }
        }

    override fun toString(): String = line

    companion object {

        fun of(name: String, value: String): AppTranslation {
            return AppTranslation("    <string name=\"$name\">$value</string>")
        }
    }
}

data class UserTranslation(
    val key: String,
    val originalValue: String,
    val localizedValue: String)

fun main(argv: Array<String>) {

    val args = parseArgs(argv)

    val originalTranslations = parseAppTranslations(args.originalStringsFile)
    val localizedTranslations = parseAppTranslations(args.localizedStringsFile).toMutableList()

    val userTranslations = parseUserTranslations(args.userTranslationsFile)

    val warnings = ArrayList<String>()

    // process
    for (userTranslation in userTranslations) {

        val original = originalTranslations.firstOrNull { it.name == userTranslation.key }
            ?: continue
        val localized = localizedTranslations.firstOrNull { it.name == userTranslation.key }

        if (localized != null) {
            if (userTranslation.originalValue == original.value) {
                localized.value = userTranslation.localizedValue
            } else {
                warnings.add("${userTranslation.key} translation (${userTranslation.originalValue}) does not match original translation in app's resources (${original.value}).")
            }
        } else if (userTranslation.localizedValue.isNotEmpty()) {
            localizedTranslations.add(localizedTranslations.size - 1, AppTranslation.of(original.name!!, userTranslation.localizedValue))
        }
    }

    // print output
    for (localizedTranslation in localizedTranslations) {
        println(localizedTranslation)
    }

    println("\nWarnings:")
    for (warning in warnings) {
        println(warning)
    }
}

fun parseArgs(argv: Array<String>): Args {

    val args = Args()

    JCommander.newBuilder()
        .addObject(args)
        .build()
        .parse(*argv)

    return args
}

private fun parseAppTranslations(filename: String): List<AppTranslation> = Files.readAllLines(Paths.get(filename)).map { AppTranslation(it) }

private fun parseUserTranslations(filename: String): List<UserTranslation> {

    val parser = CSVParserBuilder()
        .withEscapeChar(Char.MIN_VALUE) // Set this otherwise '\' is ignored.
        .build()

    val reader = InputStreamReader(FileInputStream(filename), Charset.forName("UTF-8"))
    return reader.use {
        reader.readLines().map { parser.parseLine(it) }.map { line -> UserTranslation(line[0], line[1], line[2]) }
    }
}