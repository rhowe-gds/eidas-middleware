/*
 * Copyright (c) 2018 Governikus KG. Licensed under the EUPL, Version 1.2 or as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence"); You may not use this work except
 * in compliance with the Licence. You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl Unless required by applicable law or agreed to in writing,
 * software distributed under the Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package de.governikus.eumw.eidasdemo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateEncodingException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;

import de.governikus.eumw.eidasstarterkit.*;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.XMLParserException;


/**
 * Provide the demo connector metadata
 */
@Slf4j
@Controller
public class Metadata
{

  /**
   * Just use demo in this scenario
   */
  private static final String DEMO = "demo";

  /**
   * Provides utility methods
   */
  private final SamlExampleHelper helper;

  /**
   * Default constructor for spring autowiring
   */
  public Metadata(SamlExampleHelper helper)
  {
    this.helper = helper;
  }

  /**
   * Show the eIDAS connector metadata for this demo application
   */
  @GetMapping("/Metadata")
  public void createDemoConnectorMetadata(HttpServletRequest request, HttpServletResponse response)
  {
    final String ownURL = request.getRequestURL().toString();
    final String postEndpoint = ownURL.replace("Metadata", "NewReceiverServlet");
    final EidasContactPerson eidasContactPerson = new EidasContactPerson(DEMO, DEMO, DEMO, DEMO, DEMO);
    final EidasSigner signer = new EidasSigner(true, XMLSignatureHandler.getCredential(helper.demoSignatureKey, helper.demoSignatureCertificate));
    try
    {
      byte[] metadata = EidasSaml.createMetaDataNode("eIDASSAMLDemo",
                                                     ownURL,
                                                     validForOneYear(),
                                                     helper.demoSignatureCertificate,
                                                     helper.demoDecryptionKeyPair.getCert(),
                                                     new EidasOrganisation(DEMO, DEMO, DEMO, DEMO),
                                                     eidasContactPerson,
                                                     eidasContactPerson,
                                                     postEndpoint,
                                                     EidasRequestSectorType.PUBLIC,
                                                     Arrays.asList(EidasNameIdType.PERSISTENT,
                                                                   EidasNameIdType.TRANSIENT,
                                                                   EidasNameIdType.UNSPECIFIED),
                                                     signer);
      response.getWriter().write(new String(metadata, StandardCharsets.UTF_8));
      response.setContentType("application/xml");
      response.setCharacterEncoding(String.valueOf(StandardCharsets.UTF_8));
    }
    catch (CertificateEncodingException | IOException | MarshallingException | TransformerException
      | SignatureException | InitializationException | XMLParserException | UnmarshallingException
      | ComponentInitializationException e)
    {
      log.error("Cannot provide metadata", e);
    }
  }

  private static Date validForOneYear()
  {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, 1);
    return cal.getTime();
  }
}
