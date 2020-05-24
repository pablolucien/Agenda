// ******************************** package
package net.asintec.migrator.preprocessors;

// ******************************** imports
import org.pclg.tools.ImageTools;
import org.pclg.tools.ToolBox;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
	devuelve una imagen de Adra en formato BMP
	@author El Coyote Cojo
	@version 2001.13.11
	@see net.asintec.migrator.Migrator
*/
public class PreprocessorImage implements Preprocessor {
	/** informa de lo que es capaz de hacer este señor */
	@Override
	public String getDescription() {
		final String description = getClass().getName()
									+ "\n\t devuelve una imagen de Adra en formato BMP"
									+ "\n\t No necesita parametros"
									;
		return(description);
	}

	/**
		Obtiene los posibles parametros que utilizará este preprocesador
		En esta clase no tiene utilidad
		@param param Los parametros
	*/
	@Override
	public void setParameters(final String param) {
	}

	/**
		Procesa a 'data', presumiblemente segun lo que informa getDescription()
		@param data El objeto a procesar
		@return El resultado de procesar 'data'
	*/
	@Override
	public Object process(final Object data) {
		if(data == null) {
			return (null);
		}
		try {
				//pstmt.setBytes(index, (byte[]) data);
//FileOutputStream fos = new FileOutputStream("kk.jpg");
//fos.write((byte[]) data);
//fos.close();
				final Image img;
				img = new ImageIcon((byte[])data).getImage();
				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageTools.saveBitmap(img, baos);
				baos.close();
				final byte[] newData = baos.toByteArray();
//fos = new FileOutputStream("kk.bmp");
//fos.write((byte[]) newData);
//fos.close();
//System.exit(9);
		return(newData);
		}
		catch(final IOException ex) {
			ToolBox.showInfo(ex);
			return(null);
		}
	}
}
