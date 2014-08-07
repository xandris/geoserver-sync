/**
 *
 *  #%L
 *  geoserver-sync-core
 *  $Id:$
 *  $HeadURL:$
 *  %%
 *  Copyright (C) 2013 Moebius Solutions Inc.
 *  %%
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as
 *  published by the Free Software Foundation, either version 2 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program.  If not, see
 *  <http://www.gnu.org/licenses/gpl-2.0.html>.
 *  #L%
 *
 */

package com.moesol.geoserver.sync.client;




import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.input.CountingInputStream;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.util.logging.Logging;
import org.geotools.xml.Configuration;
import org.geotools.xml.Parser;
import org.opengis.feature.Feature;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.identity.Identifier;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.moesol.geoserver.sync.core.ClientReconciler;
import com.moesol.geoserver.sync.core.FeatureSha1;
import com.moesol.geoserver.sync.core.IdAndValueSha1Comparator;
import com.moesol.geoserver.sync.core.ReconcilerDelete;
import com.moesol.geoserver.sync.core.Sha1Value;
import com.moesol.geoserver.sync.core.VersionFeatures;
import com.moesol.geoserver.sync.grouper.Sha1JsonLevelGrouper;
import com.moesol.geoserver.sync.json.Sha1SyncJson;
import com.moesol.geoserver.sync.json.Sha1SyncPositionHash;

/**
 * Synchronizes this client with geoserver using sha1Sync filter and sha1Sync outputFormat.
 * @author hastings
 */
public class GeoserverClientSynchronizer extends AbstractClientSynchronizer {
    private final Configuration m_configuration;

    /**
	 * Run the geoserver to client init-sync algorithm using sha1Sync filter and outputFormats plugged
	 * into geoserver.
	 * 
	 * The postTemplate should include the sha1Sync function like so:
	 * <pre>
	 * &lt;wfs:GetFeature 
     *  service="WFS" 
     *  version="1.1.0"
     *  outputFormat="${outputFormat}"
     *  xmlns:cdf="http://www.opengis.net/cite/data"
     *  xmlns:ogc="http://www.opengis.net/ogc"
     *  xmlns:wfs="http://www.opengis.net/wfs"&gt;
     *   &lt;wfs:Query typeName="cite:Buildings"&gt;
     *    &lt;ogc:Filter&gt;
     *     &lt;ogc:PropertyIsEqualTo&gt;
     *      &lt;ogc:Function name="sha1Sync"&gt;
     *       &lt;ogc:Literal&gt;${attributes}&lt;/ogc:Literal&gt;
     *       &lt;ogc:Literal&gt;${sha1Sync}&lt;/ogc:Literal&gt;
     *      &lt;/ogc:Function&gt;
     *      &lt;ogc:Literal&gt;true&lt;/ogc:Literal&gt;
     *     &lt;/ogc:PropertyIsEqualTo&gt;
     *    &lt;/ogc:Filter&gt;
     *   &lt;/wfs:Query&gt; 
     *  &lt;/wfs:GetFeature&gt;
	 * </pre> 
	 * @param configuration
	 * @param url
	 * @param postTemplate 
	 */
	public GeoserverClientSynchronizer(Configuration configuration, String url, String postTemplate) {
        super(url, postTemplate);
        m_configuration = configuration;
    }

    //	private void processError(Response response) throws IOException {
//		InputStream is = response.getResultStream();
//		try {
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			int c;
//			while ((c = is.read()) != -1) {
//				baos.write(c);
//			}
//			throw new RuntimeException("Failed: " + response.getResponseMessage() + baos.toString("UTF-8"));
//		} finally {
//			is.close();
//		}
//	}

    @Override
    protected Object parseWfs(InputStream is) throws IOException, SAXException, ParserConfigurationException {
		try {
			Parser parser = new Parser(m_configuration);
			return parser.parse(is);
		} finally {
			is.close();
		}
	}

}
