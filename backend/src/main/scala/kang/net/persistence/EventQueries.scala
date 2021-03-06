package kang.net.persistence

object EventQueries {

  val deleteById =
    """
      | UPDATE event SET available = false WHERE id = ?
    """.stripMargin

  val selectById =
    """
      | SELECT * FROM event where id = ?
    """.stripMargin

  val insert =
    """
      | INSERT INTO event (title, description, event_start, event_end) VALUES
      | (?,?,?,?) RETURNING id
    """.stripMargin

  val update =
    """
      | UPDATE event SET title = ?, description = ?, event_start = ?, event_end = ?
      | WHERE id = ?
    """.stripMargin

  val select =
    """
      | SELECT id, title, description, event_start, event_end, available FROM event
      | WHERE event_start BETWEEN ? AND ? AND available = true ORDER BY event_start
    """.stripMargin
}
