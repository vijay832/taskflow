import { useEffect, useState } from "react";
import api from "../api/api";

export default function TaskForm({ existingTask, onSaved }) {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [status, setStatus] = useState("TODO");

  useEffect(() => {
    if (existingTask) {
      setTitle(existingTask.title);
      setDescription(existingTask.description || "");
      setStatus(existingTask.status);
    }
  }, [existingTask]);

  const submit = async (e) => {
    e.preventDefault();
    const payload = { title, description, status, dueDate: null };
    if (existingTask) {
      await api.put(`/tasks/${existingTask.id}`, payload);
    } else {
      await api.post("/tasks", payload);
    }
    setTitle(""); setDescription(""); setStatus("TODO");
    onSaved();
  };

  return (
    <form onSubmit={submit} style={{ marginBottom: 20 }}>
      <input placeholder="Title" value={title} onChange={(e) => setTitle(e.target.value)} required />
      <input placeholder="Description" value={description} onChange={(e) => setDescription(e.target.value)} />
      <select value={status} onChange={(e) => setStatus(e.target.value)}>
        <option value="TODO">To do</option>
        <option value="IN_PROGRESS">In progress</option>
        <option value="DONE">Done</option>
      </select>
      <button type="submit">{existingTask ? "Update" : "Add"} Task</button>
    </form>
  );
}
