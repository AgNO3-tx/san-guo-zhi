# Swing Algorithm Visualization Design

## Goal

Upgrade the Java Swing dashboard so the selected algorithm modules demonstrate how results are computed, not only the final text output. The GUI must remain pure Swing/AWT with no added technology stack or external visualization library.

## Scope

The visualization work covers these dashboard entries:

- Straw Boats original rule
- Fortress Attack BFS enemy path
- Food Harvesting basic route
- Red Cliff on Fire cluster count
- Hua Rong Road escape path
- Dynamic Straw Boats
- Food Harvesting Extra
- Optimized Fire Points

## User Experience

Each covered module gets a process visualization area attached to its existing module screen. The existing controls and text output stay available, but the visual area becomes the main demonstration surface.

Each process panel supports:

- Play / pause
- Next step
- Reset
- A short current-step explanation
- Highlighted current node, grid cell, edge, direction, or candidate choice
- Final result highlighting after the process finishes

The right-side dashboard remains organized by the current feature list. When a user selects one of the covered modules, the module shows the relevant controls, visualization panel, and text output together.

## Visualization Behavior

### Fortress Attack BFS

Show the battlefield graph with circular numbered nodes and edges. During playback, highlight the current BFS node, candidate neighbors, visited nodes, queue additions, and parent links. When BFS finishes, highlight the recovered shortest path. If multiple shortest paths exist, allow switching between them after completion.

### Food Harvesting Basic

Show the same battlefield graph. Step through the base route and mark each inspected node. Nodes without food turn gray. Accepted nodes are connected into the adjusted route. The final path is shown with a stronger highlighted line.

### Hua Rong Road

Show the maze as a grid. Walls, open cells, start, and exit use distinct colors. During playback, highlight BFS expansion from the current cell to neighboring cells. Visited cells remain lightly colored. After reaching the exit, draw the final escape path.

### Red Cliff on Fire

Show the 0/1 battle matrix as a grid. Empty cells stay muted. Ship cells are discovered through cluster scanning. While scanning, highlight the current cell and color each connected cluster as it is found. The final view labels cluster groups and displays the total fireball count.

### Optimized Fire Points

Reuse the fire grid visualization. For each cluster, step through candidate ignition points and show their spread coverage. The best point is marked with a cross or bright dot after evaluation.

### Straw Boats and Dynamic Straw Boats

Show a top-down straw boat panel. Each round displays the incoming arrow wave, evaluates candidate directions, highlights the selected direction, draws arrow lines toward the boat, updates direction use counts, and increments total arrows. The original and dynamic modes share the visual component but use different rule labels and usage limits.

### Food Harvesting Extra

For production maximization, show candidate team comparison as a compact visual score track and highlight the chosen team. For guarded camp simulation, show three colored general routes on the battlefield graph, revealing each route step by step.

## Architecture

Add small GUI-facing trace models rather than changing core algorithms heavily:

- `VisualizationStep`: describes one display step, including title, detail text, highlighted nodes/cells/edges, and optional metrics.
- `StepPlaybackPanel`: reusable Swing control wrapper for play, pause, next, reset, and current-step text.
- Domain-specific panels:
  - `GraphProcessVisualizer`
  - `GridProcessVisualizer`
  - enhanced `BoatVisualizer`

Trace builders can live in the UI package or a small service-adjacent package. They should call existing services and sample data, then produce replayable steps for the GUI.

## Testing

Tests should focus on trace generation and dashboard wiring rather than pixel-perfect painting:

- Each covered feature produces a non-empty step list.
- BFS trace includes visit, neighbor/queue, and final path steps.
- Boat traces include one step per arrow wave and a final total step.
- Fire traces include cluster discovery steps.
- Maze trace includes start, expansion, and final path steps.
- Dashboard feature selection creates a visualization component for covered modules.

Manual verification should launch the Swing dashboard and check that playback controls work for the covered modules.

## Constraints

- Use only Java Swing/AWT and existing project code.
- Keep text output available for reporting.
- Do not rewrite unrelated service logic.
- Do not touch generated `.class` files.
- Avoid broad redesign of non-covered modules.
