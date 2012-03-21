package hcursor.download;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Mp3ListContentHandler extends DefaultHandler
	{
		private List<Mp3Info> infos=null;
		private Mp3Info mp3Info=null;
		private String tagName=null;

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
				String temp=new String(ch,start,length);
				if(tagName.equals("id")){
				mp3Info.setId(temp);
				}else if(tagName.equals("mp3.name")){
				mp3Info.setMp3Name(temp);
				System.out.println(temp);
				}else if(tagName.equals("mp3.size")){
				mp3Info.setMp3Size(temp);
				}else if(tagName.equals("font.dl")){
				mp3Info.setfontdl(temp);
				}else if(tagName.equals("font.img")){
				mp3Info.setfontimg(temp);
				}else if(tagName.equals("font.detail_img")){
				mp3Info.setfontdetails_img(temp);
				}
				}

		@Override
		public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
		}

		@Override
		public void endElement(String uri, String localName, String qName)
		throws SAXException {
		if(localName.equals("resource")){
		//此部分应注意
		System.out.println("-----------");
		infos.add(mp3Info);
		}
		tagName="";
		}

		public Mp3ListContentHandler(List<Mp3Info> infos) {
		super();
		this.infos = infos;
		}

		public List<Mp3Info> getInfos() {
		return infos;
		}

		public void setInfos(List<Mp3Info> infos) {
		this.infos = infos;
		}

		@Override
		public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
		}

		public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		this.tagName=localName;
		if(tagName.equals("resource")){
		mp3Info=new Mp3Info();
		}
		}

	}
