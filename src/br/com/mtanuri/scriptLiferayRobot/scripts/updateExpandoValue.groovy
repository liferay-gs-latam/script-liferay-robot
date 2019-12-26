import com.liferay.portal.kernel.dao.jdbc.DataAccess

newValue = '$time'

query = "update EXPANDOVALUE set data_ = '"+newValue+"' where columnid = (select columnid from EXPANDOCOLUMN where name = 'last-stg-publication')"

con = DataAccess.getConnection()
stmt = con.createStatement()
rs = stmt.executeQuery(query)

DataAccess.cleanUp(con, stmt, rs)