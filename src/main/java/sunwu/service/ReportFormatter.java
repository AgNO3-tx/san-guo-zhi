package sunwu.service;

import sunwu.domain.AbilityType;
import sunwu.domain.ArrowPlanResult;
import sunwu.domain.General;
import sunwu.domain.HierarchyNode;
import sunwu.domain.Point;
import sunwu.domain.TeamSuggestion;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ReportFormatter {
    public String formatHierarchy(HierarchyView view) {
        StringBuilder builder = new StringBuilder();
        appendTreeNode(builder, view.root(), "", true, true);
        builder.append("\nDepartment assignment\n");
        view.departmentMembers().forEach((department, members) -> {
            builder.append(department).append("\n");
            members.stream()
                .sorted(Comparator.comparing(General::name))
                .forEach(general -> builder.append("  - ").append(formatGeneral(general)).append("\n"));
        });
        return builder.toString();
    }

    public String formatRoster(List<General> generals) {
        StringBuilder builder = new StringBuilder("Wu generals\n");
        generals.stream()
            .sorted(Comparator.comparing(General::name))
            .forEach(general -> builder.append("- ").append(formatGeneral(general)).append("\n"));
        return builder.toString();
    }

    public String formatSortedGenerals(List<General> generals, AbilityType abilityType) {
        StringBuilder builder = new StringBuilder();
        builder.append("Sorted by ").append(label(abilityType)).append(" (high to low)\n");
        generals.stream()
            .sorted(Comparator.comparingInt((General general) -> general.ability(abilityType)).reversed()
                .thenComparing(General::name))
            .forEach(general -> builder
                .append(String.format("%-14s %s=%d", general.name(), label(abilityType), general.ability(abilityType)))
                .append(" | ")
                .append(formatCompactAbilities(general))
                .append("\n"));
        return builder.toString();
    }

    public String formatTeamRankings(List<TeamSuggestion> teams, int limit) {
        StringBuilder builder = new StringBuilder();
        builder.append("Rank | Total | Grade | Members\n");
        builder.append("---------------------------------------------\n");
        for (int index = 0; index < Math.min(limit, teams.size()); index++) {
            TeamSuggestion team = teams.get(index);
            builder.append(String.format(
                "%4d | %5d | %-5s | %s%n",
                index + 1,
                team.totalAbility(),
                team.grade(),
                team.members().stream().map(General::name).collect(Collectors.joining(", "))
            ));
        }
        return builder.toString();
    }

    public String formatTeamSuggestion(TeamSuggestion teamSuggestion) {
        return "Selected ability: " + label(teamSuggestion.focus()) + "\n"
            + "Best team: " + teamSuggestion.members().stream().map(General::name).collect(Collectors.joining(", ")) + "\n"
            + "Total: " + teamSuggestion.totalAbility() + "\n"
            + "Grade: " + teamSuggestion.grade() + "\n";
    }

    public String formatArrowPlan(String title, ArrowPlanResult result) {
        StringBuilder builder = new StringBuilder(title).append("\n");
        builder.append("Wave | Direction | Captured arrows\n");
        builder.append("----------------------------------\n");
        for (int index = 0; index < result.directions().size(); index++) {
            builder.append(String.format(
                "%4d | %-9s | %d%n",
                index + 1,
                result.directions().get(index),
                result.arrowsReceived().get(index)
            ));
        }
        builder.append("Total = ").append(result.totalArrows()).append("\n");
        return builder.toString();
    }

    public String formatPaths(String title, List<List<Integer>> paths) {
        StringBuilder builder = new StringBuilder(title).append("\n");
        for (List<Integer> path : paths) {
            builder.append("  ").append(formatPath(path)).append("\n");
        }
        return builder.toString();
    }

    public String formatFoodSimulation(FoodSimulationResult result) {
        StringBuilder builder = new StringBuilder();
        if (!result.path().isEmpty()) {
            builder.append("Path: ").append(formatPath(result.path())).append("\n");
        }
        if (!result.assignedTeam().isEmpty()) {
            builder.append("Assigned team: ")
                .append(result.assignedTeam().stream().map(General::name).collect(Collectors.joining(", ")))
                .append("\n");
        }
        if (result.totalFood() > 0) {
            builder.append("Total food: ").append(result.totalFood()).append("\n");
        }
        if (result.totalCost() > 0) {
            builder.append("Total cost: ").append(result.totalCost()).append("\n");
        }
        for (Map.Entry<String, List<Integer>> entry : result.generalRoutes().entrySet()) {
            builder.append(entry.getKey()).append(": ").append(formatPath(entry.getValue())).append("\n");
        }
        return builder.toString();
    }

    public String formatGrid(String title, int[][] grid) {
        StringBuilder builder = new StringBuilder(title).append("\n");
        for (int[] row : grid) {
            for (int col = 0; col < row.length; col++) {
                if (col > 0) {
                    builder.append(' ');
                }
                builder.append(row[col]);
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public String formatClusterSummary(int[][] grid, ClusterAnalysisResult result) {
        return formatGrid("Fire Matrix", grid)
            + "Cluster count: " + result.clusterCount() + "\n"
            + "Optimal ignition points: " + result.optimalIgnitionPoints() + "\n";
    }

    public String formatMazeWithPath(int[][] maze, MazeEscapeResult result) {
        StringBuilder builder = new StringBuilder("Hua Rong Road Maze\n");
        for (int row = 0; row < maze.length; row++) {
            for (int col = 0; col < maze[row].length; col++) {
                Point point = new Point(row, col);
                String symbol;
                if (result.start().equals(point)) {
                    symbol = "S";
                } else if (result.exit().equals(point)) {
                    symbol = "E";
                } else if (result.path().contains(point)) {
                    symbol = "*";
                } else {
                    symbol = Integer.toString(maze[row][col]);
                }
                if (col > 0) {
                    builder.append(' ');
                }
                builder.append(symbol);
            }
            builder.append("\n");
        }
        builder.append("Path: ").append(result.path()).append("\n");
        return builder.toString();
    }

    public String formatWeightedPath(WeightedPathResult result) {
        return "Path: " + formatPath(result.path()) + "\n"
            + String.format("Total time: %.3f%n", result.totalTime());
    }

    public String formatGeneral(General general) {
        return general.name() + " [" + general.armyType() + "] "
            + formatCompactAbilities(general);
    }

    public String label(AbilityType abilityType) {
        return switch (abilityType) {
            case LEADERSHIP -> "Leadership";
            case STRENGTH -> "Strength";
            case INTELLIGENCE -> "Intelligence";
            case POLITIC -> "Politic";
            case HIT_POINT -> "Hit Point";
        };
    }

    private void appendTreeNode(StringBuilder builder, HierarchyNode node, String prefix, boolean tail, boolean root) {
        if (root) {
            builder.append(node.value().name()).append("\n");
        } else {
            builder.append(prefix).append(tail ? "`-- " : "|-- ").append(node.value().name()).append("\n");
        }

        List<HierarchyNode> children = node.children();
        for (int index = 0; index < children.size(); index++) {
            boolean childTail = index == children.size() - 1;
            String nextPrefix = root ? "" : prefix + (tail ? "    " : "|   ");
            appendTreeNode(builder, children.get(index), nextPrefix, childTail, false);
        }
    }

    private String formatCompactAbilities(General general) {
        return "STR=" + general.strength()
            + ", LDR=" + general.leadership()
            + ", INT=" + general.intelligence()
            + ", POL=" + general.politic()
            + ", HP=" + general.hitPoint();
    }

    private String formatPath(List<Integer> path) {
        return path.stream().map(String::valueOf).collect(Collectors.joining(" -> "));
    }
}
