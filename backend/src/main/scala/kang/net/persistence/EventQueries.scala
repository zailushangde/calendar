package kang.net.persistence

object EventQueries {

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
      |SELECT id, title, description, event_start, event_end FROM event
      |WHERE event_start BETWEEN ? AND ? ORDER BY event_start
    """.stripMargin
}
