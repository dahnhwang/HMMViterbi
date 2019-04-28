
public class FilePath {

	String pathStr = FilePath.class.getResource("").getPath();

	public String getPathStr() {
		return pathStr;
	}

	public void setPathStr(String pathStr) {
		this.pathStr = pathStr;
	}

}
