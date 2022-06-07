package ru.mywork.taskmanager.service;

import ru.mywork.taskmanager.model.Task;

import java.util.*;

import static java.util.Collections.addAll;

class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> customLinkedList = new HashMap<>();
    public Node<Task> head;
    public Node<Task> tail;
    private int id = 0;

    @Override
    public void add(Task task) {//eсли задача есть - удаляем ее и добавляем в конец двусявязного списка
    if (customLinkedList.containsValue(linkLast(task))){
        customLinkedList.v;
    }
        customLinkedList.put(id+1,linkLast(task));
    }

    @Override
    public void remove(int id) {

    }
//}

//class CustomLinkedList<T> {

    @Override
    public Map<Integer, Task> getHistory() {
        return new HashMap(customLinkedList);
    }

    public Node linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        if (oldTail != null)
            oldTail.next = newNode;
        else head = newNode;
        id++;
        return newNode;
        //Добавляем задачу в конец списка
    }

    public void getTasks(List customLinkedList, List standartArrayList) {
        standartArrayList.addAll(customLinkedList);


        //Собирать все задачи из списка в обычный ArrayList
    }

    public void removeNode(Node node) {

        /**  public E remove(int index) {
         checkElementIndex(index);
         return unlink(node(index));
         }
         private void checkElementIndex(int index) {
         if (!isElementIndex(index))
         throw new IndexOutOfBoundsException(outOfBoundsMsg(index));

         private boolean isPositionIndex(int index) {
         return index >= 0 && index <= size;
         }

         E unlink(Node<E> x) {
         // assert x != null;
         final E element = x.item;
         final LinkedList.Node<E> next = x.next;
         final LinkedList.Node<E> prev = x.prev;

         if (prev == null) {
         first = next;
         } else {
         prev.next = next;
         x.prev = null;
         }

         if (next == null) {
         last = prev;
         } else {
         next.prev = prev;
         x.next = null;
         }

         x.item = null;
         size--;
         modCount++;
         return element;
         }
         //удалить узел */
    }


}

class Node<T> {
    public T data;
    public Node<T> next;
    public Node<T> prev;

    public Node(Node<T> prev, T data, Node<T> next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }

}



