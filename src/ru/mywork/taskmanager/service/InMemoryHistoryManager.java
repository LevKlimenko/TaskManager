package ru.mywork.taskmanager.service;

import ru.mywork.taskmanager.model.Task;

import java.util.*;

class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> customLinkedList = new HashMap<>();
    public Node first;
    public Node last;
    private List<Task> historyTask = new ArrayList<>();
    private int size = 0;

    @Override
    public void add(Task task) {//eсли задача есть - удаляем ее и добавляем в конец двусявязного списка
        remove(task.getId());
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        if (customLinkedList.containsKey(id)) {
            removeNode(customLinkedList.get(id));
            customLinkedList.remove(id);
        }
    }

    public List<Task> getHistory() {//собираем задачи из списка в обычный ArrayList
        List<Task> history = new LinkedList<>();
        if (customLinkedList.isEmpty()) {
            System.out.println("Итория пуста");
        } else {
            //System.out.println(customLinkedList);
            for (Node node : customLinkedList.values()) {
                history.add(node.data);
            }
        }
        return history;
    }

    public void linkLast(Task task) {
        if (size == 0) {
            first = new Node(null, task, null);
            last = first;
        } else {
            final Node previousNode = last;
            final Node newNode = new Node(previousNode, task, null);
            if (previousNode != null)
                previousNode.next = newNode;
            //Добавляем задачу в конец списка
            customLinkedList.put(task.getId(), newNode);
        }
        size++;

    }

    public void getTasks() {
        for (Task task : getHistory()) {
            historyTask.add(task);
        }

    }


    public void removeNode(Node node) {
        if (node == null) {
            for (Node x = first; x != null; x = x.next) {
                if (x.data == null) {
                    unlink(x);
                    return;
                }
            }
        } else {
            for (Node x = first; x != null; x = x.next) {
                if (node.equals(x.data)) {
                    unlink(x);
                    return;
                }
            }
        }
    }

    public void unlink(Node node) {
        // assert x != null;
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
        size--;
    }


}

class Node {
    public Task data;
    public Node next;
    public Node prev;

    public Node(Node prev, Task data, Node next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }

}



