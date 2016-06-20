/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.terracotta.testing.rules;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.ClassRule;
import org.junit.Test;
import org.terracotta.connection.Connection;
import org.terracotta.connection.ConnectionException;

import com.tc.util.Assert;


public class ThreadCountStabilityClassRuleIT {
  @ClassRule
  public static final Cluster CLUSTER = new BasicExternalCluster(new File("target/cluster"), 1);

  @Test
  public void test1() throws ConnectionException, IOException {
    runOneTest(1);
  }

  @Test
  public void test2() throws ConnectionException, IOException {
    runOneTest(2);
  }

  @Test
  public void test3() throws ConnectionException, IOException {
    runOneTest(3);
  }

  @Test
  public void test4() throws ConnectionException, IOException {
    runOneTest(4);
  }

  private void runOneTest(int number) throws ConnectionException, IOException {
    Map<Thread, StackTraceElement[]> stacksBefore = Thread.getAllStackTraces();
    int threadsBefore = stacksBefore.size();
    openCloseConnection();
    Map<Thread, StackTraceElement[]> stacksAfter = Thread.getAllStackTraces();
    int threadsAfter = stacksAfter.size();
    System.out.println(number + ") BEFORE: " + threadsBefore + " versus AFTER: " + threadsAfter);
    for (Thread beforeThread : stacksBefore.keySet()) {
      Assert.assertTrue(stacksAfter.containsKey(beforeThread));
    }
    for (Thread afterThread : stacksAfter.keySet()) {
      if (!stacksBefore.containsKey(afterThread)) {
        System.err.println("NEW THREAD: " + afterThread);
        for (StackTraceElement element : stacksAfter.get(afterThread)) {
          System.err.println("\t" + element);
        }
      }
    }
  }

  private void openCloseConnection() throws ConnectionException, IOException {
    Connection connection = CLUSTER.newConnection();
    try {
      //do nothing
    } finally {
      connection.close();
    }
  }
}
