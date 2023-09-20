package org.example;


import java.util.*;


public class GarbageCollectorImplementation implements GarbageCollector {

  @Override
  public List<ApplicationBean> collect(HeapInfo heap, StackInfo stack) {
    Map<String, ApplicationBean> beans = heap.getBeans();
    Deque<StackInfo.Frame> frames = stack.getStack();
    Set<ApplicationBean> reachableBeans = new HashSet<>();

    frames.forEach(frame ->
            frame.getParameters().forEach(p ->
                    addReachableBeans(reachableBeans, p)));

    List<ApplicationBean> actualGarbage = new ArrayList<>();

    for(Map.Entry<String, ApplicationBean> entry: beans.entrySet()) {
      ApplicationBean currentBean = entry.getValue();
      if(!reachableBeans.contains(currentBean)) {
        actualGarbage.add(currentBean);
      }
    }
    return actualGarbage;
  }

  private void addReachableBeans(Set<ApplicationBean> reachableObjects, ApplicationBean bean) {
    if(!reachableObjects.contains(bean)) {
      reachableObjects.add(bean);
      for(ApplicationBean parameter: bean.getFieldValues().values()) {
        addReachableBeans(reachableObjects, parameter);
      }
    }
  }

}

