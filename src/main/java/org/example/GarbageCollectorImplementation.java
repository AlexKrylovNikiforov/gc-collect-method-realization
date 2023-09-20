package org.example;


import java.util.*;


public class GarbageCollectorImplementation implements GarbageCollector {

  /*
  идея в том, что объекты, которые содержатся в куче, могут считаться живыми только только если на них есть ссылка из стека, живыми являются также все объекты,
  которые непосредственно содердатся в стеке. Поэтому мы проверяем каждый объект из стека, и рекурсивно - объекты, составляющие его поля, на предмет наличия ссылок на какие-либо
  объекты из кучи, и если такие есть - объект из кучи помещается в reachableBeans (маркируется как живой), остальные объекты являются неживыми и могут быть удалены
   */
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
  /*
  в данном методе мы проходим в итерации по сету из объектов, которые мы считаем "живыми" и если объект отсутствует среди живых, проверяем также рекурсивно все его поля
   */
  private void addReachableBeans(Set<ApplicationBean> reachableObjects, ApplicationBean bean) {
    if(!reachableObjects.contains(bean)) {
      reachableObjects.add(bean);
      for(ApplicationBean parameter: bean.getFieldValues().values()) {
        addReachableBeans(reachableObjects, parameter);
      }
    }
  }

}

