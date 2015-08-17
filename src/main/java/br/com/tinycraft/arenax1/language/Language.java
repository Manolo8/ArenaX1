package br.com.tinycraft.arenax1.language;

import br.com.tinycraft.arenax1.ArenaConfig;
import br.com.tinycraft.arenax1.ArenaX1;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Properties;

/**
 *
 * @author Willian
 */
public class Language
{

    private Properties prop;
    private HashMap<String, MessageFormat> formats;

    public Language(ArenaX1 plugin, ArenaConfig config)
    {
        formats = new HashMap();
        prop = new Properties();
        InputStream is = null;

        try
        {

            is = plugin.getResource(config.getLanguage() + ".properties");
            
            if (is.available() == 0)
            {
                is = plugin.getResource("PT-BR.properties");
            }
            prop.load(is);
        } catch (IOException ex)
        {
            ex.printStackTrace();
        } finally
        {
            try
            {
                if (is != null)
                {
                    is.close();
                }
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public String getMessage(String key, String[] infos)
    {
        try
        {
        if (this.formats.containsKey(key))
        {
            return this.formats.get(key).format(infos);
        } else
        {
            MessageFormat mF = new MessageFormat(this.prop.getProperty(key));
            this.formats.put(key, mF);
            return mF.format(infos);
        }
        } catch (Exception e)
        {
            return ("Message not found");
        }
    }
}
