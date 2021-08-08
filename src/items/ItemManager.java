package items;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import shopping.Item;
import shopping.ItemDetailView;
import shopping.ItemSummaryView;

public class ItemManager implements ItemSummaryView, ItemDetailView {
	private static Connection getConnection() throws URISyntaxException, SQLException {
		// heroku configに設定されている値を取得。
		URI dbUri = new URI(System.getenv("CLEARDB_DATABASE_URL"));
		// :をデリミタとして必要な情報を抜き取る。
		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		// JDBC用のURLを生成。
		String dbUrl = "jdbc:mysql://" + dbUri.getHost() + dbUri.getPath();
		return DriverManager.getConnection(dbUrl, username, password);
	}

	public List<Item> itemList() {
		List<Item> itemList = new ArrayList<Item>();
		try {
			// ドライバの読み込み。
			Class.forName("com.mysql.cj.jdbc.Driver");
			// データベースへ接続のためprivateメソッドを呼び出す。

			String sql = "SELECT PRODUCT_CD, PRODUCT_NM FROM PRODUCT";
			try (Connection conn = getConnection();
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(sql);) {
				while (rs.next()) {

					Item item = new Item();
					item.setProductCd(rs.getString("PRODUCT_CD"));
					item.setProductNm(rs.getString("PRODUCT_NM"));

					itemList.add(item);
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();

		}
		return itemList;

	}

	@Override
	public Item itemDetail(String itemCd) {
		Item item = new Item();
		try {
			// ドライバの読み込み。
			Class.forName("com.mysql.cj.jdbc.Driver");
			// データベースへ接続のためprivateメソッドを呼び出す。
			String sql = "SELECT PRODUCT_CD, PRODUCT_NM, DESCRIPTION FROM PRODUCT WHERE PRODUCT_CD = ?";
			try (Connection conn = getConnection();
					PreparedStatement pSmt = conn.prepareStatement(sql)) {
				pSmt.setString(1, itemCd);

				ResultSet rs = pSmt.executeQuery();

				while (rs.next()) {

					item.setProductCd(rs.getString("PRODUCT_CD"));
					item.setProductNm(rs.getString("PRODUCT_NM"));
					item.setDescription(rs.getString("DESCRIPTION"));
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();

		}
		return item;
	}

}
