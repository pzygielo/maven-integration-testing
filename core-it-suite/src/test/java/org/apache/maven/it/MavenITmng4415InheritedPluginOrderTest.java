package org.apache.maven.it;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * This is a test set for <a href="http://jira.codehaus.org/browse/MNG-4415">MNG-4415</a>.
 * 
 * @author Benjamin Bentmann
 */
public class MavenITmng4415InheritedPluginOrderTest
    extends AbstractMavenIntegrationTestCase
{

    public MavenITmng4415InheritedPluginOrderTest()
    {
        super( "[2.0.5,)" );
    }

    /**
     * Test that merging of plugins during inheritance follows these rules regarding ordering:
     * parent: X ->      A -> B ->      D -> E
     * child:       Y -> A ->      C -> D ->      F
     * result: X -> Y -> A -> B -> C -> D -> E -> F
     */
    public void testit()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-4415" );

        Verifier verifier = newVerifier( new File( testDir, "sub" ).getAbsolutePath() );
        verifier.setAutoclean( false );
        verifier.deleteDirectory( "target" );
        verifier.executeGoal( "validate" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        Properties props = verifier.loadProperties( "target/it.properties" );
        assertNotNull( props.getProperty( "project.build.plugins" ) );

        List expected = new ArrayList();
        expected.add( "maven-it-plugin-error" );
        expected.add( "maven-it-plugin-configuration" );
        expected.add( "maven-it-plugin-dependency-resolution" );
        expected.add( "maven-it-plugin-packaging" );
        expected.add( "maven-it-plugin-log-file" );
        expected.add( "maven-it-plugin-expression" );
        expected.add( "maven-it-plugin-fork" );
        expected.add( "maven-it-plugin-touch" );

        List actual = new ArrayList();

        int count = Integer.parseInt( props.getProperty( "project.build.plugins" ) );
        for ( int i = 0; i < count; i++ )
        {
            actual.add( props.getProperty( "project.build.plugins." + i + ".artifactId" ) );
        }

        actual.retainAll( expected );

        assertEquals( actual, expected );
    }

}
