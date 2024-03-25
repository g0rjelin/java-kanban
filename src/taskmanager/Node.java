package taskmanager;

import taskmodel.Task;

public class Node {

    private Task task;
    private Node next;
    private Node prev;


    Node(Node prev, Task task, Node next) {
        this.task = task;
        this.prev = prev;
        this.next = next;
    }

    Task getTask() {
        return task;
    }

    Node getNext() {
        return next;
    }

    void setNext(Node next) {
        this.next = next;
    }

    Node getPrev() {
        return prev;
    }

    void setPrev(Node prev) {
        this.prev = prev;
    }
}
