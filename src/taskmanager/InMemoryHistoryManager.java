package taskmanager;

import taskmodel.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    /**
     * двусвязный список просмотренных задач
     */
    private HashMap<Integer, Node> history;

    /**
     * Узел первой добавленной в историю задачи
     */
    private Node head;

    /**
     * Узел последней добавленной в историю задачи
     */
    private Node tail;

    InMemoryHistoryManager() {
        history = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            Integer id = task.getId();
            removeNode(history.get(id));
            history.put(id, linkLast(task));
        }
    }

    @Override
    public void remove(int id) {
        removeNode(history.get(id));
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(getTasks());
    }

    private Node linkLast(Task task) {
        Node newNode = new Node(tail, task, null);
        if (tail == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }
        tail = newNode; //"хвост" начинает ссылаться на новую созданную ноду
        return newNode;
    }

    private List<Task> getTasks() {
        List<Task> resultList = new ArrayList<>(history.size());
        Node currentNode = head;
        while (currentNode != null) {
            resultList.add(currentNode.task);
            currentNode = currentNode.next;
        }
        return resultList;
    }

    private void removeNode(Node node) {
        if (node == null) return;
        Node prevNode = node.prev;
        Node nextNode = node.next;
        if (prevNode == null && nextNode == null) {
            head = null;
            tail = null;
            history.clear();
        } else {
            if (prevNode != null) {
                prevNode.next = nextNode;
            } else {
                head = nextNode;
                nextNode.prev = null;
            }
            if (nextNode != null) {
                nextNode.prev = prevNode;
            } else {
                tail = prevNode;
                prevNode.next = null;
            }
            history.remove(node.task.getId());
        }
    }

    private static class Node {

        private Task task;
        private Node next;
        private Node prev;


        Node(Node prev, Task task, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }

    }

}

