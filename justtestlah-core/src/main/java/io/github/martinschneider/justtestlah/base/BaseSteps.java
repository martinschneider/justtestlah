package io.github.martinschneider.justtestlah.base;

import io.github.martinschneider.justtestlah.testdata.TestDataMap;
import org.springframework.beans.factory.annotation.Autowired;

/** Base class for all steps. */
public class BaseSteps extends Base {

  @Autowired private TestDataMap testDataMap;

  protected <T> T testdata(Class<T> type) {
    return testdata(type, "default");
  }

  protected <T> T testdata(Class<T> type, String name) {
    return testDataMap.get(type, name);
  }

  // TODO!
  //    protected String testdata(String name)
  //    {
  //        return testDataMap.getIndividualProperty(name);
  //    }
}
