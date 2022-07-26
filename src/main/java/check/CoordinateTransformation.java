package check;

import org.locationtech.proj4j.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CoordinateTransformation {
    public void transformStart(Connection connection){
        String tableName = "public.game";
        int count =1;
        try {
            Statement stmt = connection.createStatement();

            ResultSet resultSet = stmt.executeQuery("SELECT seq,la,lo FROM "+tableName);
            //resultset이 주소를 가르킨다 아마도
            Statement stmt2 = connection.createStatement();
            connection.setAutoCommit(false);
            connection.setSavepoint();
            while (resultSet.next()){
                System.out.println("seq"+resultSet.getString("seq"));
                ProjCoordinate transform = transform(resultSet.getDouble("la"),resultSet.getDouble("lo"));
                System.out.println("위도 경도 변환:"+transform.x+","+transform.y);
                stmt2.execute("UPDATE public.game SET la ="+transform.y+", lo ="+transform.x+"WHERE seq ="+resultSet.getString("seq"));
                connection.commit();
//                System.out.println(stmt2.execute("UPDATE public.smoke SET la ="+transform.x+", lo ="+transform.y+"WHERE seq ="+resultSet.getString("seq")+""));
                System.out.println("카운트:"+count);
                count++;
            }
            stmt.close();
            stmt2.close();
            connection.close();
            System.out.println("종료");
        }catch (SQLException e ){
            System.out.println(e);
            try {
                connection.rollback();
            }catch (SQLException e1){
                System.out.println(e1);
            }
        }
        catch (IllegalStateException a1){
            System.out.println(a1);
            try {
                System.out.println("롤백");
                connection.rollback();
            }catch (SQLException e1){
                System.out.println(e1);
            }
        }
    }

    public  ProjCoordinate transform(Double x, Double y){
        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CRSFactory csFactory = new CRSFactory();

        CoordinateReferenceSystem E4326 = csFactory.createFromParameters("E4326", "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs");
        CoordinateReferenceSystem E5174 = csFactory.createFromParameters("E5174", "+proj=tmerc +lat_0=38 +lon_0=127.0028902777778 +k=1 +x_0=200000 +y_0=500000 +ellps=bessel +units=m +no_defs +towgs84=-115.80,474.99,674.11,1.16,-2.31,-1.63,6.43");
        CoordinateReferenceSystem E2097 = csFactory.createFromParameters("E2097", "+proj=tmerc +lat_0=38 +lon_0=127 +k=1 +x_0=200000 +y_0=500000 +ellps=bessel +units=m +no_defs +towgs84=-115.80,474.99,674.11,1.16,-2.31,-1.63,6.43");
        CoordinateTransform trans = ctFactory.createTransform(E5174, E4326);
        ProjCoordinate p = new ProjCoordinate();
        ProjCoordinate p2 = new ProjCoordinate();

        p.x = x;
        p.y = y;
        return trans.transform(p,p2);
    }
}
