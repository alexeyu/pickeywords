package nl.alexeyu.photomate.service;


public interface PrioritizedTask {

    public enum TaskPriority {
        
        HIGH, MEDIUM, LOW;

    }

    TaskPriority getPriority();

}
