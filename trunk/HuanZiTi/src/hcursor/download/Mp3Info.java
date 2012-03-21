package hcursor.download;

public class Mp3Info
	{
		private String id;
		private String mp3Name;
		private String mp3Size;
		private String fontdl;
		private String fontimg;
		private String fontdetail_img;

		public Mp3Info() {
		super();
		}

		public Mp3Info(String id, String mp3Name, String mp3Size, String fontdl,String fontimg,String fontdetail_img) {
		super();
		this.id = id;
		this.mp3Name = mp3Name;
		this.mp3Size = mp3Size;
		this.fontdl = fontdl;
		this.fontimg = fontimg;
		this.fontdetail_img = fontdetail_img;
		}
		public String getId() {
		return id;
		}

		@Override
		public String toString() {
		return "Mp3Info [id=" + id + ", fontdl=" + fontdl + ", fontimg="
		+ fontimg + ", mp3Name=" + mp3Name + ", mp3Size=" + mp3Size + ", fontdetails_img=" + fontdetail_img +"";
		}
		public void setId(String id) {
		this.id = id;
		}

		public String getMp3Name() {
		return mp3Name;
		}

		public void setMp3Name(String mp3Name) {
		this.mp3Name = mp3Name;
		}

		public String getMp3Size() {
		return mp3Size;
		}

		public void setMp3Size(String mp3Size) {
		this.mp3Size = mp3Size;
		}

		public String getfontdl() {
		return fontdl;
		}

		public void setfontdl(String fontdl) {
		this.fontdl = fontdl;
		}

		public String getfontimg() {
		return fontimg;
		}

		public void setfontimg(String fontimg) {
		this.fontimg = fontimg;
		}
		
		public String getfontdetail_img() {
		return fontdetail_img;
		}

		public void setfontdetails_img(String fontdetail_img) {
		this.fontdetail_img = fontdetail_img;
		}

	}
