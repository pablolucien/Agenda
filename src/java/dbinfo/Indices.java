// ******************************** package
package dbinfo;

// ******************************** imports
// Utilities ad hoc

/**
	Indices <BR>
	@author El Coyote Cojo
	@version 2002.jun.27 18:41:32, CEST
*/
public class Indices {
	// ******************************** Variables de clase

	// ******************************** Variables de instancia

	// ******************************** Constructores

	/**
		Constructor por omision
		--author El Coyote Cojo
		--version 2002.jun.27 18:41:32, CEST
	*/
	private Indices() {
		//getIndexInfo("catalog", "schema", "table", true, true);
		getIndexInfo(null, null, "sites", true, false);
	}

	// ******************************** Metodos de instancia

    void getIndexInfo(final String s, final String s1, final String s2,
		final boolean flag, final boolean flag1)
    {
        final String s3 = "analyze table " + (s1 != null ? s1 + "." : "") + s2 + (flag1 ? " estimate statistics" : " compute statistics");
        final String s4 = "select null as table_cat,\n       owner as table_schem,\n       table_name,\n   " +
"    0 as NON_UNIQUE,\n       null as index_qualifier,\n       null as index_name" +
", 0 as type,\n       0 as ordinal_position, null as column_name,\n       null as" +
" asc_or_desc,\n       num_rows as cardinality,\n       blocks as pages,\n       " +
"null as filter_condition\nfrom all_tables\nwhere table_name = '"
 + s2 + "'\n";
        String s5 = "";
        if(s1 != null && s1.length() > 0)
        {
            s5 = "  and owner = '" + s1 + "'\n";
        }
        final String s6 = "select null as table_cat,\n       i.owner as table_schem,\n       i.table_name,\n" +
"       decode (i.uniqueness, 'UNIQUE', 0, 1),\n       null as index_qualifier,\n" +
"       i.index_name,\n       1 as type,\n       c.column_position as ordinal_pos" +
"ition,\n       c.column_name,\n       null as asc_or_desc,\n       i.distinct_ke" +
"ys as cardinality,\n       i.leaf_blocks as pages,\n       null as filter_condit" +
"ion\nfrom all_indexes i, all_ind_columns c\nwhere i.table_name = '"
 + s2 + "'\n";
        String s7 = "";
        if(s1 != null && s1.length() > 0)
        {
            s7 = "  and i.owner = '" + s1 + "'\n";
        }
        String s8 = "";
        if(flag)
        {
            s8 = "  and i.uniqueness = 'UNIQUE'\n";
        }
        final String s9 = "  and i.index_name = c.index_name\n  and i.table_owner = c.table_owner\n  and i." +
"table_name = c.table_name\n  and i.owner = c.index_owner\n"
;
        final String s10 = "order by non_unique, type, index_name, ordinal_position\n";
        final String s11 = s4 + s5 + "union\n" + s6 + s7 + s8 + s9 + s10;
		System.out.println(s11);
    }

	// ******************************** Metodos estaticos

	/**
		Ayuda al usuario
		--author El Coyote Cojo
		--version 2002.jun.27 18:41:32, CEST
	*/
	public static void usage(final String [] args) {
		System.err.println("Usage: java Indices " +  "");
		System.exit(1);
	}

	/**
		Ejecuta la aplicación
		--author El Coyote Cojo
		--version 2002.jun.27 18:41:32, CEST
	*/
	public static void main(final String [] args) {
		new Indices();
	}
}
