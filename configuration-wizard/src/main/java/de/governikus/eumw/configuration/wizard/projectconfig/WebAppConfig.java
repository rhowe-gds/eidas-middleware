/*
 * Copyright (c) 2018 Governikus KG. Licensed under the EUPL, Version 1.2 or as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence"); You may not use this work except
 * in compliance with the Licence. You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl Unless required by applicable law or agreed to in writing,
 * software distributed under the Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package de.governikus.eumw.configuration.wizard.projectconfig;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import de.governikus.eumw.configuration.wizard.projectconfig.filter.Utf8Filter;
import de.governikus.eumw.configuration.wizard.web.ExposedReloadableResourceBundleMessageSource;
import lombok.extern.slf4j.Slf4j;


/**
 * this class represents the core configuration for this application. It setups the component scanning and
 * should define globally setup beans for the spring framework
 */
@Slf4j
@Configuration
@ComponentScan({"de.governikus.eumw.configuration.wizard.projectconfig",
                "de.governikus.eumw.configuration.wizard.web.controller",
                "de.governikus.eumw.configuration.wizard.web.handler",
                "de.governikus.eumw.configuration.wizard.web.converter"})
public class WebAppConfig implements WebMvcConfigurer
{

  /**
   * Path to the resources
   */
  @Value("${config.bundles.dir:classpath:/resource_bundles}")
  private String bundlesDir;

  /**
   * this value is an optional value that will tell us where the configuration should be stored in the end or
   * read at system startup
   */
  @Value("${configDirectory:}")
  private String configDirectory;

  /**
   * this bean is used to have the configDirectory value as a dynamic value that can be
   *
   * @return the bean with the configuration directory
   */
  @Bean
  public ConfigDirectory getConfigDirFactoryBean()
  {
    return new ConfigDirectory(configDirectory);
  }

  /**
   * @return a filter that sets the encoding of any request and any response to UTF-8
   */
  @Bean
  public Filter utf8Filter()
  {
    return new Utf8Filter();
  }

  /**
   * this method holds all the resource-bundle paths that will be used
   * 
   * @return all the resource-bundle paths
   */
  public String[] resourceBundles()
  {
    // @formatter:off
    return new String[]{bundlesDir + "/statusMessages",
                        bundlesDir + "/commonMessages"};
    // @formatter:on
  }

  /**
   * this method will load the resource-bundles for localization
   * 
   * @return the message source that contains the messages of the resource-bundles.
   */
  @Bean("messageSource")
  public ExposedReloadableResourceBundleMessageSource messageSource()
  {
    final ExposedReloadableResourceBundleMessageSource messageSource = new ExposedReloadableResourceBundleMessageSource();
    messageSource.setBasenames(resourceBundles());
    messageSource.setUseCodeAsDefaultMessage(false);
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }

}
