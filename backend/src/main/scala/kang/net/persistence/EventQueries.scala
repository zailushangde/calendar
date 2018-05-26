package kang.net.persistence

object EventQueries {

  val insert =
    """
      | INSERT INTO event (title, description, event_start, event_end) VALUES
      | (?,?,?,?)
    """.stripMargin

  val select =
    """
      |SELECT id, title, description, event_start, event_end FROM event
      |WHERE event_start BETWEEN ? AND ? ORDER BY event_start
    """.stripMargin
}
