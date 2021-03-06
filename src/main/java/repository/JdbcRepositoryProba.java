package repository;

import domain.Arbitru;
import domain.Proba;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.TipProba;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class JdbcRepositoryProba implements IRepository<Integer, Proba> {
    private JdbcUtils dbUtils;
    private static final Logger logger= LogManager.getLogger();

    public JdbcRepositoryProba(Properties props) {
        logger.info("Initializing DBRepo with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }

    @Override
    public int size()  {
        logger.traceEntry();
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("select count(*) as [SIZE] from Probe")) {
            try(ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    logger.traceExit(result.getInt("SIZE"));
                    return result.getInt("SIZE");
                }
            }
        }catch(SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        return 0;
    }

    @Override
    public void save(Proba entity) {
        logger.traceEntry("saving proba {} ",entity);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("insert into Probe values (?,?,?)")){
            preStmt.setInt(1,entity.getId());
            preStmt.setString(2,entity.getNumeArbitru());
            preStmt.setString(3,entity.getTipProba().toString());
            int result=preStmt.executeUpdate();
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        logger.traceExit();

    }

    @Override
    public void delete(Integer id) {
        logger.traceEntry("deleting proba with id: {}",id);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("delete from Probe where id=?")){
            preStmt.setInt(1,id);
            int result=preStmt.executeUpdate();
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        logger.traceExit();
    }

    @Override
    public void update(Integer id, Proba entity) {
        //todo
    }

    @Override
    public Proba findOne(Integer id) {
        logger.traceEntry("finding proba with id: {} ",id);
        Connection con=dbUtils.getConnection();

        try(PreparedStatement preStmt=con.prepareStatement("select * from Probe where id=?")){
            preStmt.setInt(1,id);
            try(ResultSet result=preStmt.executeQuery()) {
                if (result.next()) {
                    int idd = result.getInt("id");
                    String numeArbitru = result.getString("numeArbitru");
                    String tipProba=result.getString("tipProba");
                    Proba proba = new Proba(idd,numeArbitru, TipProba.valueOf(tipProba));
                    logger.traceExit(proba);
                    return proba;
                }
            }
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        logger.traceExit("No arbitru found with name {}", id);

        return null;
    }

    @Override
    public Iterable<Proba> findAll() {
        //logger.();
        Connection con=dbUtils.getConnection();
        List<Proba> probe=new ArrayList<>();
        try(PreparedStatement preStmt=con.prepareStatement("select * from Probe")) {
            try(ResultSet result=preStmt.executeQuery()) {
                while (result.next()) {
                    int idd = result.getInt("id");
                    String numeArbitru = result.getString("numeArbitru");
                    String tipProba=result.getString("tipProba");
                    Proba proba = new Proba(idd,numeArbitru,TipProba.valueOf(tipProba));
                    probe.add(proba);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB "+e);
        }
        logger.traceExit(probe);
        return probe;
    }

}
