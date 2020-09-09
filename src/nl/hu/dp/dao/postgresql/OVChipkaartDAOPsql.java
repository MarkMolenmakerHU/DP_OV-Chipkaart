package nl.hu.dp.dao.postgresql;

import nl.hu.dp.dao.OVChipkaartDAO;
import nl.hu.dp.dao.ReizigerDAO;
import nl.hu.dp.domain.OVChipkaart;
import nl.hu.dp.domain.Reiziger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OVChipkaartDAOPsql implements OVChipkaartDAO {

    private Connection connection;
    private ReizigerDAO rdao;

    public OVChipkaartDAOPsql(Connection connection, ReizigerDAO rdao) {
        this.connection = connection;
        this.rdao = rdao;
    }

    @Override
    public boolean save(OVChipkaart ovChipkaart) throws SQLException {
        // Save Reiziger if he doesn't exist
        if (rdao.findById(ovChipkaart.getReiziger().getId()) == null)
            return rdao.save(ovChipkaart.getReiziger());

        // SQL Query
        String query = "INSERT INTO ov_chipkaart (kaart_nummer, geldig_tot, klasse, saldo, reiziger_id) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, ovChipkaart.getKaart_nummer());
        preparedStatement.setDate(2, Date.valueOf(ovChipkaart.getGeldig_tot()));
        preparedStatement.setInt(3, ovChipkaart.getKlasse());
        preparedStatement.setDouble(4, ovChipkaart.getSaldo());
        preparedStatement.setInt(5, ovChipkaart.getReiziger().getId());

        // Query uitvoeren
        int affectedRows = preparedStatement.executeUpdate();

        // Sluit alles
        preparedStatement.close();

        // Return
        return affectedRows > 0;
    }

    @Override
    public boolean update(OVChipkaart ovChipkaart) throws SQLException {
        // SQL Query
        String query = "UPDATE ov_chipkaart SET geldig_tot = ?, klasse = ?, saldo = ? WHERE kaart_nummer = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setDate(1, Date.valueOf(ovChipkaart.getGeldig_tot()));
        preparedStatement.setInt(2, ovChipkaart.getKlasse());
        preparedStatement.setDouble(3, ovChipkaart.getSaldo());
        preparedStatement.setInt(4, ovChipkaart.getKaart_nummer());

        // Query uitvoeren
        int affectedRows = preparedStatement.executeUpdate();

        // Sluit alles
        preparedStatement.close();

        // Return
        return affectedRows > 0;
    }

    @Override
    public boolean delete(OVChipkaart ovChipkaart) throws SQLException {
        // SQL Query
        String query = "DELETE FROM ov_chipkaart where kaart_nummer = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, ovChipkaart.getKaart_nummer());

        // Query uitvoeren
        int affectedRows = preparedStatement.executeUpdate();

        // Sluit alles
        preparedStatement.close();

        // Return
        return affectedRows > 0;
    }

    @Override
    public OVChipkaart findByKaartNummer(int kaart_nummer) throws SQLException {
        // SQL Query
        String query = "SELECT * FROM ov_chipkaart WHERE kaart_nummer = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, kaart_nummer);

        // Resultaten
        ResultSet resultSet = preparedStatement.executeQuery();

        int reiziger_id, klasse;
        Date geldig_tot;
        double saldo;
        OVChipkaart ovChipkaart = null;

        while (resultSet.next()) {

            klasse = resultSet.getInt("klasse");
            geldig_tot = resultSet.getDate("geldig_tot");
            saldo = resultSet.getDouble("saldo");
            reiziger_id = resultSet.getInt("reiziger_id");

            ovChipkaart = new OVChipkaart(kaart_nummer, geldig_tot.toLocalDate(), klasse, saldo, rdao.findById(reiziger_id));

        }
        // Sluit alles
        resultSet.close();
        preparedStatement.close();

        // Return
        return ovChipkaart;
    }

    @Override
    public List<OVChipkaart> findByReiziger(Reiziger reiziger) throws SQLException {
        // SQL Query
        String query = "SELECT * FROM ov_chipkaart WHERE reiziger_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, reiziger.getId());

        // Resultaten
        ResultSet resultSet = preparedStatement.executeQuery();

        int kaart_nummer, klasse;
        Date geldig_tot;
        double saldo;
        List<OVChipkaart> ovChipkaarten = new ArrayList<>();

        while (resultSet.next()) {

            kaart_nummer = resultSet.getInt("kaart_nummer");
            klasse = resultSet.getInt("klasse");
            geldig_tot = resultSet.getDate("geldig_tot");
            saldo = resultSet.getDouble("saldo");

            OVChipkaart ovChipkaart = new OVChipkaart(kaart_nummer, geldig_tot.toLocalDate(), klasse, saldo, reiziger);
            ovChipkaarten.add(ovChipkaart);

        }
        // Sluit alles
        resultSet.close();
        preparedStatement.close();

        // Return
        return ovChipkaarten;
    }

    @Override
    public List<OVChipkaart> findAll() throws SQLException {
        // SQL Query
        String query = "SELECT * FROM ov_chipkaart";
        Statement statement = connection.createStatement();

        // Resultaten
        ResultSet resultSet = statement.executeQuery(query);

        int reiziger_id, kaart_nummer, klasse;
        Date geldig_tot;
        double saldo;
        List<OVChipkaart> ovChipkaarten = new ArrayList<>();

        while (resultSet.next()) {

            kaart_nummer = resultSet.getInt("kaart_nummer");
            klasse = resultSet.getInt("klasse");
            geldig_tot = resultSet.getDate("geldig_tot");
            saldo = resultSet.getDouble("saldo");
            reiziger_id = resultSet.getInt("reiziger_id");

            OVChipkaart ovChipkaart = new OVChipkaart(kaart_nummer, geldig_tot.toLocalDate(), klasse, saldo, rdao.findById(reiziger_id));
            ovChipkaarten.add(ovChipkaart);

        }
        // Sluit alles
        resultSet.close();
        statement.close();

        // Return
        return ovChipkaarten;
    }
}
