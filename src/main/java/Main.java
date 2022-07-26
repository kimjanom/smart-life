import check.CoordinateTransformation;
import check.CreateDB;
import check.DataInspection;
import org.locationtech.proj4j.ProjCoordinate;

public class Main {
    private Properties properties;
    DataInspection dataInspection = new DataInspection();
    CoordinateTransformation coordinateTransformation = new CoordinateTransformation();

    //    CreateDB createDB = new CreateDB();
    public static void main(String[] arguments) {
        new Main().start();
    }

    public void start() {
        Properties.initialize();
        properties = Properties.getInstance();

        PostgresConnection.initialize();
//        createDB.makeDb(PostgresConnection.getPostgres());
        coordinateTransformation.transformStart(PostgresConnection.getPostgres());
//        dataInspection.inspectionStart(PostgresConnection.getPostgres());

    }


}
