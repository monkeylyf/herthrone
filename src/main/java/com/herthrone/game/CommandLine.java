package com.herthrone.game;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.herthrone.base.Card;
import com.herthrone.base.Creature;
import com.herthrone.base.Minion;
import com.herthrone.base.Spell;
import com.herthrone.configuration.TargetConfig;
import com.herthrone.constant.ConstCommand;
import com.herthrone.constant.ConstTarget;
import com.herthrone.constant.ConstType;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by yifengliu on 5/15/16.
 */
public class CommandLine {

  private static boolean stdoutOn = true;

  public static CommandNode yieldCommands(final Battlefield battlefield) {
    final Side mySide = battlefield.mySide;
    final Side opponentSide = battlefield.opponentSide;

    final CommandNode root = new CommandNode("root");
    // Populate play card option.
    final CommandNode playCardNode = new CommandNode(ConstCommand.PLAY_CARD.toString());
    for (int i = 0; i < mySide.hand.size(); ++i) {
      final Card card = mySide.hand.get(i);
      if (mySide.manaCrystal.getCrystal() >= card.getCrystalManaCost().getVal()) {
        playCardNode.addChildNode(new CommandNode(card.view().toString(), i));
      }
    }
    if (playCardNode.childOptions.size() != 0) {
      root.addChildNode(playCardNode);
    }
    // Populate move minions option.
    final CommandNode moveMinions = new CommandNode(ConstCommand.MOVE_MINION.toString());
    for (int i = 0; i < mySide.board.size(); ++i) {
      final Minion minion = mySide.board.get(i);
      if (minion.canMove()) {
        final CommandNode moveMinionCommand = new CommandNode(minion.getCardName(), i, ConstTarget.OWN);
        for (int j = 0; j < opponentSide.board.size(); ++j) {
          final Minion opponentMinion = opponentSide.board.get(j);
          if (GameManager.isMinionTargetable(opponentMinion, opponentSide.board, ConstType.ATTACK)) {
            moveMinionCommand.addChildNode(new CommandNode(opponentMinion.getCardName(), j, ConstTarget.OPPONENT));
          }
        }
        final CommandNode heroNode = new CommandNode(opponentSide.hero.getCardName(), -1);
        heroNode.setSide(ConstTarget.OPPONENT);
        moveMinionCommand.addChildNode(heroNode);
        moveMinions.addChildNode(moveMinionCommand);
      }
    }
    if (mySide.hero.canMove()) {
      // TODO: add hero as an legit attack move.
    }
    if (moveMinions.childOptions.size() != 0) {
      root.addChildNode(moveMinions);
    }
    // Use hero power.
    if (battlefield.mySide.hero.getAttackMovePoints().getVal() > 0 && battlefield.mySide.manaCrystal.getCrystal() >= battlefield.mySide.hero.getHeroPower().getCrystalManaCost().getVal()) {
      final Spell heroPower = mySide.hero.getHeroPower();
      final CommandNode useHeroPower = new CommandNode(ConstCommand.USE_HERO_POWER.toString());

      scanTargets(useHeroPower, heroPower.getTargetConfig(), battlefield);
      root.addChildNode(useHeroPower);
    }
    // End turn.
    root.addChildNode(new CommandNode(ConstCommand.END_TURN.toString()));

    return root;
  }

  private static void scanTargets(final CommandNode root, final Optional<TargetConfig> heroConfig, final Battlefield battlefield) {
    if (heroConfig.isPresent()) {
      TargetConfig config = heroConfig.get();
      switch (config.scope) {
        case OWN:
          scanTargets(config, battlefield.mySide, ConstTarget.OWN).stream().forEach(node -> root.addChildNode(node));
          break;
        case OPPONENT:
          scanTargets(config, battlefield.opponentSide, ConstTarget.OPPONENT).stream().forEach(node -> root.addChildNode(node));
          break;
        case ALL:
          scanTargets(config, battlefield.mySide, ConstTarget.OWN).stream().forEach(node -> root.addChildNode(node));
          scanTargets(config, battlefield.opponentSide, ConstTarget.OPPONENT).stream().forEach(node -> root.addChildNode(node));
          break;
        default:
          throw new RuntimeException("Unknown scope: " + config.scope.toString());
      }
    }
  }

  private static List<CommandNode> scanTargets(final TargetConfig config, final Side side, final ConstTarget target) {
    List<CommandNode> nodes = new ArrayList<>();
    switch (config.type) {
      case HERO:
        nodes.add(new CommandNode(side.hero.toString(), -1, target));
        break;
      case MINION:
        for (int i = 0; i < side.board.size(); ++i) {
          nodes.add(new CommandNode(side.board.get(i).toString(), i, target));
        }
        break;
      case ALL:
        nodes.add(new CommandNode(side.hero.toString(), -1, target));
        for (int i = 0; i < side.board.size(); ++i) {
          nodes.add(new CommandNode(side.board.get(i).toString(), i, target));
        }
        break;
    }

    return nodes;
  }

  public static Creature toTargetCreature(final Battlefield battlefield, final CommandNode node) {
    Preconditions.checkNotNull(node.getSide(), "Unknown side");
    final Side side = targetToSide(battlefield, node);
    return (node.index == -1) ? side.hero : side.board.get(node.index);
  }

  private static Side targetToSide(final Battlefield battlefield, final CommandNode node) {
    Preconditions.checkNotNull(node.getSide(), "Unknown side");
    switch (node.getSide()) {
      case OWN:
        return battlefield.mySide;
      case OPPONENT:
        return battlefield.opponentSide;
      default:
        throw new RuntimeException("Unknown side: " + node.getSide().toString());
    }
  }

  public static CommandNode run(CommandNode cursor) {
    return run(cursor, System.in);
  }

  static CommandNode run(CommandNode cursor, final InputStream input) {
    boolean outOfTime = false;
    final Scanner scanner = new Scanner(input);
    while (!cursor.isLeaf() && !outOfTime) {
      cursor.listChildOptions();
      cursor = cursor.move(scanner);
      println("--------------------");
    }
    // TODO: Intentionally not to close scanner because it closes System.in as well.
    //scanner.close();
    return cursor;
  }

  public static void println(final Object object) {
    if (stdoutOn) {
      System.out.println(object);
    }
  }

  public static void turnOffStdout() {
    stdoutOn = false;
  }

  public static class CommandNode {

    private static final String TEMPLATE = "%d. %s.";
    public final String option;
    public final List<CommandNode> childOptions;
    public final int index;
    private CommandNode parent = null;
    private ConstTarget targetSide = null;

    public CommandNode(final String option) {
      this(option, -1);
    }

    public CommandNode(final String option, final int index) {
      this(option, index, ConstTarget.OPPONENT);
    }

    public CommandNode(final String option, final int index, final ConstTarget type) {
      this.option = option;
      this.childOptions = new ArrayList<>();
      this.index = index;
      this.targetSide = type;
    }

    public ConstTarget getSide() {
      return targetSide;
    }

    public void setSide(final ConstTarget side) {
      targetSide = side;
    }

    public void listChildOptions() {
      for (int i = 0; i < childOptions.size(); ++i) {
        // 1-based index.
        String output = String.format(CommandNode.TEMPLATE, i + 1, childOptions.get(i));
        println(output);
      }

      if (parent != null) {
        println(String.format(CommandNode.TEMPLATE, 0, "Previous list"));
      }
    }

    public String toString() {
      return Objects.firstNonNull(option, "");
    }

    public void addChildNode(final CommandNode node) {
      childOptions.add(node);
      node.parent = this;
    }

    public CommandNode move(final Scanner scanner) {
      println("Enter a number:");
      int index = 0;
      while (true) {
        try {
          index = scanner.nextInt();
          if (isValidOptionNum(index)) {
            break;
          } else {
            println("Invalid input.");
          }
        } catch (java.util.InputMismatchException err) {
          println("Invalid input.");
        }
      }
      if (index == 0) {
        return previous();
      } else {
        return next(index);
      }
    }

    public boolean isValidOptionNum(final int n) {
      // Either in range [1, size] or 0 when has previous list(parent).
      return (1 <= n && n <= childOptions.size()) || (parent != null && n == 0);
    }

    private CommandNode previous() {
      return Objects.firstNonNull(parent, this);
    }

    private CommandNode next(final int index) {
      // 1-based index.
      return childOptions.get(index - 1);
    }

    public boolean isLeaf() {
      return childOptions.size() == 0;
    }

    public String getParentType() {
      return parent.option;
    }

    public CommandNode getParent() {
      return parent;
    }
  }
}
