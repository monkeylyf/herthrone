package com.herthrone.game;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import com.herthrone.base.Card;
import com.herthrone.base.Creature;
import com.herthrone.base.Minion;
import com.herthrone.configuration.TargetConfig;
import com.herthrone.constant.ConstCommand;
import com.herthrone.constant.ConstTarget;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CommandLine {

  private static boolean stdoutOn = true;
  private static final String ROOT = "root";

  public static CommandNode yieldCommands(final List<Card> candidates) {
    final CommandNode root = new CommandNode(ROOT);
    for (int i = 0; i < candidates.size(); ++i) {
      final CommandNode candidateNode = new CommandNode(candidates.get(i).cardName(), i);
      root.addChildNode(candidateNode);
    }
    return root;
  }

  public static CommandNode yieldCommands(final Side side) {
    final CommandNode root = new CommandNode(ROOT);
    // Populate play card option.
    final CommandNode playCardNode = new CommandNode(ConstCommand.PLAY_CARD.toString());
    for (int i = 0; i < side.hand.size(); ++i) {
      final Card card = side.hand.get(i);
      if (side.hero.manaCrystal().getCrystal() >= card.manaCost().value()) {
        playCardNode.addChildNode(new CommandNode(card.view().toString(), i));
      }
    }
    if (playCardNode.childOptions.size() != 0) {
      root.addChildNode(playCardNode);
    }
    // Populate minions attack option.
    final CommandNode moveMinions = new CommandNode(ConstCommand.MINION_ATTACK.toString());
    for (int i = 0; i < side.board.size(); ++i) {
      final Minion minion = side.board.get(i);
      if (minion.canMove()) {
        final CommandNode minionAttackCommand = new CommandNode(minion.cardName(), i, ConstTarget.OWN);
        for (int j = 0; j < side.getFoeSide().board.size(); ++j) {
          final Minion foeMinion = side.getFoeSide().board.get(j);
          if (foeMinion.isAttackTarget()) {
            final CommandNode target = new CommandNode(foeMinion.cardName(), j, ConstTarget.FOE);
            minionAttackCommand.addChildNode(target);
          }
        }
        final CommandNode heroNode = new CommandNode(side.getFoeSide().hero.cardName(), -1);
        heroNode.setSide(ConstTarget.FOE);
        minionAttackCommand.addChildNode(heroNode);
        moveMinions.addChildNode(minionAttackCommand);
      }
    }

    // Populate hero attack option if hero can attack.
    if (side.hero.canMove()) {
      final CommandNode heroAttack = new CommandNode(ConstCommand.HERO_ATTACK.toString());
      for (int j = 0; j < side.getFoeSide().board.size(); ++j) {
        final Minion foeMinion = side.getFoeSide().board.get(j);
        if (foeMinion.isAttackTarget()) {
          heroAttack.addChildNode(new CommandNode(foeMinion.cardName(), j, ConstTarget.FOE));
        }
      }
    }
    if (moveMinions.childOptions.size() != 0) {
      root.addChildNode(moveMinions);
    }
    // Populate use hero power option if can use hero power.
    if (side.hero.heroPowerMovePoints().isPositive() &&
        !side.hero.getHeroPower().manaCost().isGreaterThan(side.hero.manaCrystal().getCrystal())) {
      final CommandNode useHeroPower = new CommandNode(ConstCommand.USE_HERO_POWER.toString());

      scanTargets(useHeroPower, side.hero.getHeroPower().getSelectTargetConfig(), side);
      root.addChildNode(useHeroPower);
    }
    // Populate end turn option.
    root.addChildNode(new CommandNode(ConstCommand.END_TURN.toString()));

    return root;
  }

  private static void scanTargets(final CommandNode root, final TargetConfig config,
                                  final Side side) {
    switch (config.scope) {
      case OWN:
        scanTargets(config, side, ConstTarget.OWN).forEach(root::addChildNode);
        break;
      case FOE:
        scanTargets(config, side.getFoeSide(), ConstTarget.FOE).forEach (root::addChildNode);
        break;
      case ALL:
        scanTargets(config, side, ConstTarget.OWN).forEach(root::addChildNode);
        scanTargets(config, side.getFoeSide(), ConstTarget.FOE).forEach(root::addChildNode);
        break;
      default:
        throw new RuntimeException("Unknown scope: " + config.scope.toString());
    }
  }

  private static List<CommandNode> scanTargets(final TargetConfig config, final Side side,
                                               final ConstTarget target) {
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

  public static Creature toTargetCreature(final Side side, final CommandNode node) {
    Preconditions.checkNotNull(node.getSide(), "Unknown side");
    final Side targetSide = targetToSide(side, node);
    return (node.index == -1) ? targetSide.hero : targetSide.board.get(node.index);
  }

  private static Side targetToSide(final Side side, final CommandNode node) {
    Preconditions.checkNotNull(node.getSide(), "Unknown side");
    switch (node.getSide()) {
      case OWN:
        return side;
      case FOE:
        return side.getFoeSide();
      default:
        throw new RuntimeException("Unknown side: " + node.getSide().toString());
    }
  }

  public static List<CommandNode> run(final CommandNode cursor, final Range<Integer> optionRange) {
    return run(cursor, System.in, optionRange);
  }

  static List<CommandNode> run(CommandNode cursor, final InputStream input,
                               final Range<Integer> optionRange) {
    final Scanner scanner = new Scanner(input);
    while (!cursor.isLeaf()) {
      cursor.listChildOptions();
      final List<CommandNode> optionNodes = cursor.move(scanner, optionRange);
      if (optionNodes.size() > 1) {
        return optionNodes;
      } else {
        cursor = optionNodes.get(0);
      }
      println("--------------------");
    }
    // Intentionally not to close scanner because it closes System.in as well, which causes
    // next this is called again, it throws.
    return Collections.singletonList(cursor);
  }

  public static void println(final Object... objects) {
    if (stdoutOn) {
      System.out.println(Joiner.on("").join(objects));
    }
  }

  public static void turnOffStdout() {
    stdoutOn = false;
  }

  static class CommandNode {

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
      this(option, index, ConstTarget.FOE);
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
      return MoreObjects.firstNonNull(option, "");
    }

    public void addChildNode(final CommandNode node) {
      childOptions.add(node);
      node.parent = this;
    }

    public List<CommandNode> move(final Scanner scanner, final Range<Integer> optionRange) {
      List<Integer> options;
      while (true) {
        if (optionRange == Game.SINGLE_COMMAND) {
          println("Enter a number:");
        } else {
          println("Enter numbers in range [1, " + optionRange.upperEndpoint() + "] (e.g., 1" +
              " 2 3):");
        }
        try {
          options = parseLine(scanner.nextLine(), optionRange);
          if (isValidOptions(options, optionRange)) {
            break;
          } else {
            println("Invalid input.");
          }
        } catch (java.util.InputMismatchException err) {
          println("Invalid input.");
        }
      }
      return (options.size() == 0) ?
          Collections.singletonList(MoreObjects.firstNonNull(parent, this)) :
          options.stream().map(index -> childOptions.get(index - 1)).collect(Collectors.toList());
    }

    private static List<Integer> parseLine(final String line, final Range<Integer> optionRange) {
      final String[] options = line.split("\\s+");
      if (!optionRange.contains(options.length)) {
        throw new java.util.InputMismatchException();
      } else {
        return Arrays.stream(options).map(Integer::parseInt).collect(Collectors.toList());
      }
    }

    private boolean isValidOptions(final List<Integer> options, final Range<Integer> optionRange) {
      if (optionRange == Game.SINGLE_COMMAND) {
        final int index = options.get(0);
        return (options.size() == 1 && 1 <= index && index <= childOptions.size()) ||
            (parent != null && options.size() == 1 && options.get(0) == 0);
      } else {
        return (new HashSet<>(options).size() != options.size()) &&
           options.stream().allMatch(optionRange::contains);
      }
    }

    private boolean isLeaf() {
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
