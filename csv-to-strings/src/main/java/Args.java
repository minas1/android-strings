import com.beust.jcommander.Parameter;

public class Args {

    @Parameter(names = "--originalFile")
    public String originalStringsFile;

    @Parameter(names = "--localizedFile")
    public String localizedStringsFile;

    @Parameter(names = "--userTranslations")
    public String userTranslationsFile;
}
