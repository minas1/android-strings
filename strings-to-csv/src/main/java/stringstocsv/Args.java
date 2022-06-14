package stringstocsv;

import com.beust.jcommander.Parameter;

class Args {

    @Parameter(names = {"-dir"}, description = "The root directory of the project.", required = true)
    String rootDirectory;
}
