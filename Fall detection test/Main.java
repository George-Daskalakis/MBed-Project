import java.io.FileNotFoundException;
/**
 * A template main-method class for use from
 * non-BlueJ IDEs or the command line.
 * 
 * @author djb
 * @version 2017.01.12
 */
public class Main
{
    /**
     * This class is not meant to be instantiated,
     * so prevent that.
     */
    private Main() 
    {
    }
    
    public static void main(String[] args) throws FileNotFoundException 
    {
        Program prog = new Program();
        prog.run();
        prog.finish();
        System.exit(0);
    }
}
