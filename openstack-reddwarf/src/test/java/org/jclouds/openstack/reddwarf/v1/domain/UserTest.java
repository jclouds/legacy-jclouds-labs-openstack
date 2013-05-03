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
package org.jclouds.openstack.reddwarf.v1.domain;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import org.testng.annotations.Test;
import com.google.common.collect.ImmutableSet;

@Test(groups = "unit", testName = "UserTest")
public class UserTest {
   public void testUserForName() {
      User user1 = forName("1");
      User user2 = forName("2");
      assertEquals(user1.getName(), "1");
      assertEquals(user2.getName(), "2");
      assertFalse(user1.equals(user2));
   }
   
   /**
    * Creates a dummy User when you need an User with just the userName.
    */
   public static User forName(String userName) {       
       return User.builder()
               .name(userName)
               .password("password")
               .databases( 
                       ImmutableSet.of(
                               Database.builder().name("db1").build(),
                               Database.builder().name("db2").build()
                               ) ).build();
   }
}
