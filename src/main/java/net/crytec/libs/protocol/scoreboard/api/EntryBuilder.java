/*
 * ************************************************************************
 *
 * AvarionCraft.de - Created at 08.12.19, 20:22	 by crysis992
 *  __________________
 *
 * [2016] - [2019] AvarionCraft.de
 * All Rights Reserved.
 * net.crytec.util.scoreboard.api.EntryBuilder can not be copied and/or distributed without the express
 *  permission of crysis992
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of AvarionCraft.de and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to AvarionCraft.de
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from AvarionCraft.de.
 *
 */

package net.crytec.libs.protocol.scoreboard.api;

import java.util.LinkedList;
import java.util.List;

public class EntryBuilder {

  private final LinkedList<Entry> entries = new LinkedList<>();

  /**
   * Append a blank line.
   *
   * @return this
   */
  public EntryBuilder blank() {
    return this.next("");
  }

  /**
   * Append a new line with specified text.
   *
   * @param string text
   * @return this
   */
  public EntryBuilder next(final String string) {
    this.entries.add(new Entry(string, this.entries.size()));
    return this;
  }

  /**
   * Returns a map of entries.
   *
   * @return map
   */
  public List<Entry> build() {
    for (final Entry entry : this.entries) {
      entry.setPosition(this.entries.size() - entry.getPosition());
    }
    return this.entries;
  }
}