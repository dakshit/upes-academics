package com.god.attendence;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayAtten extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.attenview);
		
		// Reference to the layout components
		TextView display = (TextView) findViewById(R.id.tv1);
             
		String html = getIntent().getExtras().getString("com.god.attendence.ATTPAGE");
        Document doc = Jsoup.parse(html);
		Elements tddata = doc.select("td");
		int position[] = {30,37,44,51,58,65,72,79,86,93,100,107};
		int i=0,k=0;
		
		if (tddata != null && tddata.size() > 0)
		{
			for(Element element : tddata)
			{
//				if(i==(position[k]-1))
//				{
//					display.append("\n");
//					++k;
//				}
				if(i>29)
				{
					if((i-30)%7==0)
					{
						display.append("\n");
					}
					String data= element.text();
					display.append(data+" ");
				}				
				++i;
			}
		}
	}

}
