package org.apache.ode.bpel.compiler_2_0;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;

import org.apache.ode.bpel.obj.OProcess;
import org.apache.ode.bpel.obj.migrate.DeepEqualityHelper;
import org.apache.ode.bpel.obj.migrate.DomElementComparator;
import org.apache.ode.bpel.obj.migrate.ExtensibeImplEqualityComp;
import org.apache.ode.bpel.obj.serde.DeSerializer;
import org.apache.ode.bpel.obj.serde.OmSerdeFactory;
import org.apache.ode.bpel.obj.serde.OmSerdeFactory.SerializeFormat;
import org.junit.Assert;

public class JavaSerializationTest extends GoodCompileTest{
	protected SerializeFormat format;
	protected String pathSuffix;
	
	public JavaSerializationTest(){
		this.format = OmSerdeFactory.SerializeFormat.FORMAT_SERIALIZED_JAVA;
		this.pathSuffix = "java";
	}
	public void runTest(String bpel) throws Exception {
		try {
			Class testClass = getClass();
			URL url = testClass.getResource(bpel);
			URI uri = url.toURI();
			String path = uri.getPath();
			File bpelFile = new File(path);
			OProcess origi = _compiler.compile2OProcess(bpelFile, 0);
			String bpelPath = bpelFile.getAbsolutePath();
			String cbpPath = bpelPath.substring(0, bpelPath.lastIndexOf("."))
					+ "." + pathSuffix + ".cbp";
			DeSerializer serializer = new DeSerializer();
			serializer.serialize(new FileOutputStream(cbpPath), 
					origi, format);

			DeSerializer deserializer = new DeSerializer(new File(cbpPath));
			OProcess desered = deserializer.deserialize();

    		DeepEqualityHelper de = new DeepEqualityHelper();
    		de.addCustomComparator(new ExtensibeImplEqualityComp());
    		de.addCustomComparator(new DomElementComparator());
    		boolean res = de.deepEquals(origi, desered);
			assertEquals(Boolean.TRUE, res);
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("Compilation or deserialization did not succeed.");
		}
	}

}
