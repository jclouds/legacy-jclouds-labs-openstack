/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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

package org.jclouds.openstack.reddwarf.v1.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import org.jclouds.openstack.reddwarf.v1.domain.Flavor;
import org.jclouds.openstack.reddwarf.v1.internal.BaseRedDwarfApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.google.common.collect.FluentIterable;

/**
 * @author Zack Shoylev
 */
@Test(groups = "live", testName = "FlavorApiLiveTest")
public class FlavorApiLiveTest extends BaseRedDwarfApiLiveTest {

    @Override
    @BeforeClass(groups = { "integration", "live" })
    public void setup() {
        super.setup();
    }

    private void checkFlavor(Flavor flavor) {
        assertNotNull(flavor.getId(), "Id cannot be null for " + flavor);
        assertNotNull(flavor.getName(), "Name cannot be null for " + flavor);
    }

    @Test
    public void testListFlavorsByAccount() {
        for (String zone : api.getConfiguredZones()) {
            FlavorApi flavorApi = api.getFlavorApiForZone(zone);

            FluentIterable<Flavor> response = flavorApi.list( api.getCurrentTenantId().get().getId() ); // tenant id, but referred to as account id. 
            for (Flavor flavor : response) {
                checkFlavor(flavor);
            }  
        }   
    }

    @Test
    public void testListFlavorsByAccountWhenAccountIdNotFound() {
        for (String zone : api.getConfiguredZones()) {
            FlavorApi flavorApi = api.getFlavorApiForZone(zone);
            assertTrue(flavorApi.list("9999").isEmpty());
        }
    }

    @Test
    public void testGetFlavor() {
        for (String zone : api.getConfiguredZones()) {
            FlavorApi flavorApi = api.getFlavorApiForZone(zone);           
            for (Flavor flavor : flavorApi.list()) {
                Flavor flavorFromGet = flavorApi.get(flavor.getId());
                assertEquals(flavorFromGet.getId(), flavor.getId());
                assertEquals(flavorFromGet.getRam(), flavor.getRam());
                assertEquals(flavorFromGet.getName(), flavor.getName());
                assertEquals(flavorFromGet.getLinks(), flavor.getLinks());
            }
        }
    }

    @Test
    public void testGetFlavorWhenNotFound() {
        for (String zone : api.getConfiguredZones()) {
            FlavorApi flavorApi = api.getFlavorApiForZone(zone);
            assertNull(flavorApi.get(9999));
        }
    }   
}