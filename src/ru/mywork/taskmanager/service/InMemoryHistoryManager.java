package ru.mywork.taskmanager.service;

import ru.mywork.taskmanager.model.Task;

import java.util.*;

class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> customLinkedList = new HashMap<>();
    private Node first;
    private Node last;
    private int size = 0;

    @Override
    public void add(Task task) {
        remove(task.getId());
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        if (customLinkedList.containsKey(id)) {
            removeNode(customLinkedList.get(id));
        }
    }

    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        if (customLinkedList.isEmpty()) {
            System.out.println("История пуста");
        } else {
            Node node = first;
            while (node != null) {
                history.add(node.data);
                node = node.next;
            }
        }
        return history;
    }

    private void linkLast(Task task) {
        Node node = new Node(last, task, null);
        if (last == null) {
            first = node;
        } else {
            last.next = node;
        }
        last = node;
        customLinkedList.put(task.getId(), node);
        size++;
    }

    private void removeNode(Node node) {//предыдущая реализация этого метода была взята из LinkedList
        final Task element = node.data;
        final Node next = node.next;
        final Node prev = node.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            node.prev = null;
        }
        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }
        node.data = null;
        customLinkedList.remove(element.getId());

    }

    private static class Node {
        public Task data;
        public Node next;
        public Node prev;

        public Node(Node prev, Task data, Node next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

}





