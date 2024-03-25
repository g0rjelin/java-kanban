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
        if (history.containsKey(id)) {
            removeNode(history.get(id));
            history.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(getTasks());
    }

    private Node linkLast(Task task) {
        Node oldTail = tail;
        Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            //ссылка на предыдущий "хвост" (oldTail) при создании ноды через конструктор
            oldTail.setNext(newNode);
        }
        return newNode;
    }

    private List<Task> getTasks() {
        List<Task> resultList = new ArrayList<>(history.size());
        Node currentNode = head;
        while (currentNode != null) {
            resultList.add(currentNode.getTask());
            currentNode = currentNode.getNext();
        }
        return resultList;
    }

    private void removeNode(Node node) {
        if (node == null) return;
        Node prevNode = node.getPrev();
        Node nextNode = node.getNext();
        if (prevNode == null && nextNode == null) {
            head = null;
            tail = null;
            history.clear();
        } else {
            if (prevNode != null) {
                prevNode.setNext(nextNode);
            } else {
                head = nextNode;
                nextNode.setPrev(null);
            }
            if (nextNode != null) {
                nextNode.setPrev(prevNode);
            } else {
                tail = prevNode;
                prevNode.setNext(null);
            }
        }
    }

}

