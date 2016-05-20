package com.herthrone.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by yifengliu on 5/15/16.
 */
public class CommandLine {

  public static void main(String[] args) {
    final CommandNode root = constructCommandMenu();
    run(root);
  }

  public static CommandNode constructCommandMenu() {
    CommandNode root = new CommandNode(null, "");
    CommandNode playCard = new CommandNode(root, "Play Card");
    playCard.childOptions.add(
            new CommandNode(playCard, "Fire Ball")
    );
    playCard.childOptions.add(
            new CommandNode(playCard, "Wolfrider")
    );

    CommandNode moveMinions = new CommandNode(root, "Move Minion");
    moveMinions.childOptions.add(
            new CommandNode(moveMinions, "Yeti")
    );
    moveMinions.childOptions.add(
            new CommandNode(moveMinions, "Motherfucker")
    );
    root.childOptions.add(playCard);
    root.childOptions.add(moveMinions);
    root.childOptions.add(
            new CommandNode(root, "Hero Power")
    );
    root.childOptions.add(
            new CommandNode(root, "End Turn")
    );
    return root;
  }

  public static void run(CommandNode cursor) {
    boolean outOfTime = false;
    while (!cursor.isLeaf() && !outOfTime) {
      cursor.listChildOptions();
      cursor = cursor.move();
      System.out.println("--------------------");
    }
    System.out.println("Your turn finished");
  }

  private static class CommandNode {

    private static final String TEMPLATE = "%d. %s.";
    public final CommandNode parent;
    public final String option;
    public final List<CommandNode> childOptions;

    public CommandNode(final CommandNode parent, final String option) {
      this.parent = parent;
      this.option = option;
      this.childOptions = new ArrayList<>();
    }

    public CommandNode(final String option) {
      this(null, option);
    }

    public void listChildOptions() {
      for (int i = 0; i < this.childOptions.size(); ++i) {
        // 1-based index.
        String output = String.format(CommandNode.TEMPLATE, i + 1, this.childOptions.get(i));
        System.out.println(output);
      }

      if (this.parent != null) {
        System.out.println(String.format(CommandNode.TEMPLATE, 0, "Previous list"));
      }
    }

    public boolean isValidOptionNum(final int n) {
      // Either in range [1, size] or 0 when has previous list(parent).
      return (1 <= n && n <= this.childOptions.size()) || (this.parent != null && n == 0);
    }

    private CommandNode previous() {
      return (this.parent == null) ? this : this.parent;
    }

    private CommandNode next(final int index) {
      // 1-based index.
      return this.childOptions.get(index - 1);
    }

    public String toString() {
      return (this.option == null) ? "" : this.option;
    }

    private int readInput() {
      System.out.println("Enter a number:");
      while (true) {
        Scanner reader = new Scanner(System.in);
        try {
          final int index = reader.nextInt();
          if (isValidOptionNum(index)) {
            return index;
          } else {
            System.out.println("Invalid input.");
          }
        } catch (java.util.InputMismatchException err) {
          System.out.println("Invalid input.");
        }
      }
    }

    public CommandNode move() {
      final int index = readInput();
      if (index == 0) {
        return this.previous();
      } else {
        return this.next(index);
      }
    }

    public boolean isLeaf() {
      return this.childOptions.size() == 0;
    }
  }
}
